package url.shortener.server.dto;

import io.micronaut.core.annotation.Introspected;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Introspected
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserCreateDto {

  @NotBlank
  //TODO define max length
  @Email
  private String email;

  @Size(min = 8, max = 256)
  @NotBlank
  private String password;
}
