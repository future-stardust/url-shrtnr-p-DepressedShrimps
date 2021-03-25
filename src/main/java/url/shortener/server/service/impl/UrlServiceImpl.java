package url.shortener.server.service.impl;

import io.micronaut.http.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.synonym.SynonymsSearchComponent;
import url.shortener.server.config.exception.BusinessException;
import url.shortener.server.config.exception.NotUniqueAliasException;
import url.shortener.server.config.properties.RepositoryProperties;
import url.shortener.server.config.properties.UrlProperties;
import url.shortener.server.dto.UrlCreateDto;
import url.shortener.server.dto.UrlsListDto;
import url.shortener.server.entity.ShortenedUrl;
import url.shortener.server.entity.UserUrl;
import url.shortener.server.mapper.UserUrlMapper;
import url.shortener.server.repository.UrlRepository;
import url.shortener.server.repository.UserUrlRepository;
import url.shortener.server.service.UrlService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class UrlServiceImpl implements UrlService {

  public static final int MAX_KEY_LENGTH = 10;

  private final UrlRepository urlRepository;
  private final UserUrlRepository userUrlRepository;
  private final SynonymsSearchComponent synonymsComponent;
  private final UserUrlMapper userUrlMapper;
  private final int proposalCount;
  private final int maxKeyLength;

  @Inject
  public UrlServiceImpl(
      UrlRepository urlRepository,
      UserUrlRepository userUrlRepository,
      SynonymsSearchComponent synonymsComponent,
      UserUrlMapper userUrlMapper,
      UrlProperties urlProperties,
      RepositoryProperties repositoryProperties
  ) {
    this.urlRepository = urlRepository;
    this.userUrlRepository = userUrlRepository;
    this.synonymsComponent = synonymsComponent;
    this.userUrlMapper = userUrlMapper;
    this.proposalCount = urlProperties.getProposalCount();
    this.maxKeyLength = repositoryProperties.getUrl().getKeyLength();
  }

  @Override
  public String createUrl(String userId, UrlCreateDto urlCreateDto) {
    String alias = urlCreateDto.getAlias();
    if (StringUtils.isBlank(alias)) {
      alias = saveWithGeneratedAlias(urlCreateDto.getUri());
    } else {
      saveWithGivenAlias(alias, urlCreateDto.getUri());
    }
    userUrlRepository.save(
        new UserUrl()
            .setUserId(userId)
            .setAlias(alias)
    );
    return alias;
  }

  private String saveWithGeneratedAlias(URI uri) {
    boolean isSaved = false;
    String availableAlias = "";

    while (!isSaved) {
      availableAlias = urlRepository.nextAvailableAlias();
      ShortenedUrl shortenedUrl = new ShortenedUrl()
          .setAlias(availableAlias)
          .setOriginalUrl(uri);
      isSaved = urlRepository.save(shortenedUrl);
    }
    return availableAlias;
  }

  private void saveWithGivenAlias(String alias, URI uri) {
    if (!urlRepository.existsById(alias)) {
      urlRepository.save(
          new ShortenedUrl()
              .setAlias(alias)
              .setOriginalUrl(uri)
      );
      return;
    }
    throw new NotUniqueAliasException(
        generateProposal(alias)
    );
  }

  private List<String> generateProposal(String alias) {
    List<String> synonyms = new ArrayList<>();
    if (synonymsComponent.isSearchable(alias)) {
      synonymsComponent.retrieveSynonyms(alias, proposalCount)
          .stream()
          .filter(synonym -> synonym.length() <= maxKeyLength)
          .filter(synonym -> !urlRepository.existsById(synonym))
          .forEach(synonyms::add);
    }
    if (synonyms.size() < proposalCount) {
      List<String> availableAliases = urlRepository
          .findAvailableAliases(proposalCount - synonyms.size());
      synonyms.addAll(availableAliases);
    }
    return synonyms;
  }

  @Override
  public UrlsListDto getUserUrls(String userId) {
    Set<String> aliases = new HashSet<>(userUrlRepository.findAll(userId));

    return userUrlMapper.toListDto(
        urlRepository.findAll(aliases)
    );
  }

  @Override
  public void deleteUserUrl(String userId, String alias) {
    UserUrl userUrl = new UserUrl()
        .setUserId(userId)
        .setAlias(alias);

    urlRepository.deleteById(alias);
    userUrlRepository.delete(userUrl);
  }

  @Override
  public URI getOriginalUrl(String alias) {
    return urlRepository.findById(alias)
        .map(ShortenedUrl::getOriginalUrl)
        .orElseThrow(() -> new BusinessException("Url is not found", HttpStatus.NOT_FOUND));
  }
}
