package url.shortener.server.config.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties("service")
public class ServiceProperties {

  @NotBlank
  private String host;

  @NotBlank
  private String schema;

}
