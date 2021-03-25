package url.shortener.server.component.encryption.impl;

import io.micronaut.context.annotation.Requires;
import java.util.Objects;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import url.shortener.server.component.encryption.HashingComponent;

@Singleton
@Requires(property = "encryption.hashing.dummy.enabled", value = "true", defaultValue = "true")
public class HashingComponentDummyImpl implements HashingComponent {

  @Override
  public String hash(@NotBlank String plainText) {
    return plainText;
  }

  @Override
  public boolean match(@NotBlank String hashedValue, @NotBlank String plainText) {
    return Objects.equals(hashedValue, plainText);
  }
}
