package url.shortener.server.dto;

import java.net.URI;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UrlsListDto {

  private List<UrlListItemDto> urls;

  @Data
  @Accessors(chain = true)
  public static class UrlListItemDto {

    private String alias;
    private URI originalUrl;
  }
}
