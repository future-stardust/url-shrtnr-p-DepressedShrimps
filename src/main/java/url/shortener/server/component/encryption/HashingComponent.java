package url.shortener.server.component.encryption;

import javax.validation.constraints.NotBlank;

public interface HashingComponent {

  String hash(@NotBlank String plainText);

  boolean match(@NotBlank String hashedValue, @NotBlank String plainText);
}
