package url.shortener.server.component.encryption.impl;

import io.micronaut.context.annotation.Requires;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import url.shortener.server.component.encryption.HashingComponent;
import url.shortener.server.config.properties.EncryptionProperties;

@Slf4j
@Singleton
@Requires(property = "encryption.hashing.dummy.enabled", value = "false")
public class PBKDF2HashingComponentImpl implements HashingComponent {

  private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

  private final byte[] salt;
  private final int keyLength;
  private final int countOfIterations;
  private final SecretKeyFactory secretKeyFactory;

  @SneakyThrows
  public PBKDF2HashingComponentImpl(
      EncryptionProperties encryptionProperties
  ) {
    salt = encryptionProperties.getSalt().getBytes(StandardCharsets.UTF_8);
    keyLength = encryptionProperties.getKeyLength();
    countOfIterations = encryptionProperties.getCountOfIterations();
    secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
  }

  @Override
  @SneakyThrows
  public String hash(@NotBlank String plainText) {
    PBEKeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt, countOfIterations, keyLength);
    byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();

    return StringUtils.toEncodedString(encoded, StandardCharsets.UTF_8);
  }

  @Override
  public boolean match(@NotBlank String hashedValue, @NotBlank String plainText) {
    return StringUtils.equals(hashedValue, this.hash(plainText));
  }
}
