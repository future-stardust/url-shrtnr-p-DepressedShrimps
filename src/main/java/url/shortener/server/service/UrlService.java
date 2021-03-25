package url.shortener.server.service;

import java.net.URI;
import url.shortener.server.dto.UrlCreateDto;
import url.shortener.server.dto.UrlsListDto;

public interface UrlService {

  String createUrl(String userId, UrlCreateDto urlCreateDto);

  UrlsListDto getUserUrls(String userId);

  void deleteUserUrl(String userId, String alias);

  URI getOriginalUrl(String alias);

}
