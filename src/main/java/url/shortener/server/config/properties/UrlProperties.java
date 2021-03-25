package url.shortener.server.config.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties("url")
public class UrlProperties {

  private int proposalCount;

}
