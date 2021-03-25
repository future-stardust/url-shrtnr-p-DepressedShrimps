package url.shortener.server.dto;

import io.micronaut.core.annotation.Introspected;
import java.net.URI;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Introspected
@Data
@Accessors(chain = true)
public class UrlCreateDto {

  @NotNull
  private URI uri;

  @Size(max = 10)
  @Pattern(regexp = "[A-Za-z0-9]+")
  private String alias;
}
