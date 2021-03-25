package url.shortener.server.config.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ConfigurationProperties("encryption")
public class EncryptionProperties {

  private String salt;
  private int countOfIterations;
  private int keyLength;
}
