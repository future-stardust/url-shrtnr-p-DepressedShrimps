package url.shortener.server.config.properties;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import java.time.Duration;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ConfigurationProperties("cache")
public class CacheProperties {

  @ConfigurationBuilder(configurationPrefix = "url")
  private CacheParams url = new CacheParams();

  @ConfigurationBuilder(configurationPrefix = "token")
  private CacheParams token = new CacheParams();

  @Data
  @Accessors(chain = true)
  public static class CacheParams {

    private Duration duration;
    private long maxSize;
  }
}
