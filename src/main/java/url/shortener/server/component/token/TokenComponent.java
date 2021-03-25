package url.shortener.server.component.token;

import java.util.Optional;
import javax.validation.constraints.NotBlank;

public interface TokenComponent {

  String createToken(@NotBlank String id);

  void removeToken(@NotBlank String token);

  Optional<String> validateToken(String token);
}
