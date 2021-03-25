package url.shortener.server.repository.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.config.properties.CacheProperties;
import url.shortener.server.config.properties.CacheProperties.CacheParams;
import url.shortener.server.config.properties.RepositoryProperties;
import url.shortener.server.entity.ShortenedUrl;
import url.shortener.server.repository.UrlRepository;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static url.shortener.server.service.impl.UrlServiceImpl.MAX_KEY_LENGTH;

@Slf4j
@Singleton
public class UrlRepositoryImpl implements UrlRepository {

  private static final int AVAILABLE_KEY_TRIES_MAX_ATTEMPTS = 20;

  private final BigTable urlTable;
  private final LoadingCache<String, String> cache;


  public UrlRepositoryImpl(
      @Named("urlTable") BigTable urlTable,
      CacheProperties cacheProperties,
      RepositoryProperties repositoryProperties
  ) {
    this.urlTable = urlTable;
    this.cache = initCache(urlTable, cacheProperties.getUrl());
  }

  private LoadingCache<String, String> initCache(
      BigTable urlTable,
      CacheParams cacheProperties
  ) {
    return CacheBuilder.newBuilder()
        .expireAfterAccess(cacheProperties.getDuration())
        .maximumSize(cacheProperties.getMaxSize())
        .build(new CacheLoader<>() {
          @Override
          public String load(String key) {
            return urlTable.findByKey(key).orElseThrow();
          }
        });
  }


  @Override
  public List<ShortenedUrl> findAll(@NotNull Set<String> aliases) {
    return aliases
        .stream()
        .parallel()
        .map(alias -> urlTable.findByKey(alias).map(value -> toUrl(alias, value)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private ShortenedUrl toUrl(String alias, String value) {
    return new ShortenedUrl()
        .setAlias(alias)
        .setOriginalUrl(URI.create(value));
  }

  @Override
  public Optional<ShortenedUrl> findById(@NotNull String alias) {
    try {
      return Optional.of(toUrl(alias, cache.get(alias)));
    } catch (ExecutionException | UncheckedExecutionException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean save(@NotNull ShortenedUrl shortenedUrl) {
    return urlTable.put(shortenedUrl.getAlias(), shortenedUrl.getOriginalUrl().toString());
  }

  @Override
  public void deleteById(@NotNull String alias) {
    urlTable.deleteByKey(alias);
    cache.invalidate(alias);
  }

  @Override
  public boolean existsById(@NotNull String alias) {
    return urlTable.containsKey(alias);
  }

  @Override
  public String nextAvailableAlias() {
    for (int i = 0; i < AVAILABLE_KEY_TRIES_MAX_ATTEMPTS; i++) {
      String possibleKey = RandomStringUtils.randomAlphanumeric(1, MAX_KEY_LENGTH);
      if (!urlTable.containsKey(possibleKey)) {
        return possibleKey;
      }
    }
    throw new IllegalStateException("Cannot find available alias");
  }

  @Override
  public List<String> findAvailableAliases(int count) {
    List<String> availableAliases = new ArrayList<>(count);

    for (int i = 0; i < count; i++) {
      try {
        availableAliases.add(nextAvailableAlias());
      } catch (Exception e) {
        log.error("Could not find available alias");
      }
    }
    return availableAliases;
  }
}
