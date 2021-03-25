package url.shortener.server.entity;

import java.net.URI;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShortenedUrl {

  private String alias;
  private URI originalUrl;

}
