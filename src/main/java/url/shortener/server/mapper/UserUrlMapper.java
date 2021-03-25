package url.shortener.server.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import url.shortener.server.dto.UrlsListDto;
import url.shortener.server.dto.UrlsListDto.UrlListItemDto;
import url.shortener.server.entity.ShortenedUrl;

@Mapper(config = CommonMappingConfig.class)
public abstract class UserUrlMapper {

  public UrlsListDto toListDto(List<ShortenedUrl> userUrls) {
    UrlsListDto urlsListDto = new UrlsListDto();
    List<UrlListItemDto> urls = userUrls
        .stream()
        .map(entry -> new UrlListItemDto()
            .setAlias(entry.getAlias())
            .setOriginalUrl(entry.getOriginalUrl())
        )
        .collect(Collectors.toList());

    return urlsListDto.setUrls(urls);
  }
}
