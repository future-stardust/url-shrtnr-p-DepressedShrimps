package url.shortener.server.component.token.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.bigtable.BigTable;
import url.shortener.server.component.token.TokenComponent;
import url.shortener.server.config.properties.CacheProperties;
import url.shortener.server.config.properties.CacheProperties.CacheParams;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Singleton
public class TokenComponentImpl implements TokenComponent {

  private final BigTable tokenTable;
  private final LoadingCache<String, String> tokenCache;

  public TokenComponentImpl(
      @Named("tokenTable") BigTable tokenTable,
      CacheProperties cacheProperties
  ) {
    this.tokenTable = tokenTable;
    this.tokenCache = initCache(tokenTable, cacheProperties.getToken());
  }

  private LoadingCache<String, String> initCache(
      BigTable tokenTable, CacheParams cacheProperties
  ) {
    return CacheBuilder.newBuilder()
        .expireAfterAccess(cacheProperties.getDuration())
        .maximumSize(cacheProperties.getMaxSize())
        .build(new CacheLoader<>() {
          @Override
          public String load(String key) {
            return tokenTable.findByKey(key).orElseThrow();
          }
        });
  }

  @Override
  public String createToken(@NotBlank String id) {
    String token = UUID.randomUUID().toString();
    tokenTable.put(token, id);
    return token;
  }

  @Override
  public void removeToken(@NotBlank String token) {
    tokenTable.deleteByKey(token);
    tokenCache.invalidate(token);
  }

  @Override
  public Optional<String> validateToken(String token) {
    if (StringUtils.isBlank(token)) {
      return Optional.empty();
    }
    try {
      return Optional.of(tokenCache.get(token));
    } catch (ExecutionException | UncheckedExecutionException e) {
      return Optional.empty();
    }
  }
}
