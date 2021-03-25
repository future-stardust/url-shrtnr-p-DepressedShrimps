package url.shortener.server.component.encryption.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import url.shortener.server.config.properties.EncryptionProperties;

@ExtendWith(MockitoExtension.class)
class PBKDF2HashingComponentImplTest {

  @Spy
  private final EncryptionProperties encryptionProperties = new EncryptionProperties()
      .setSalt("test_salt")
      .setCountOfIterations(256)
      .setKeyLength(65);

  @InjectMocks
  private PBKDF2HashingComponentImpl instance;

  @Test
  void hashExpectNotBlankAndDifferentValue() {
    String plainText = "Hello";

    String result = instance.hash(plainText);

    assertThat(result)
        .isNotBlank()
        .isNotEqualTo(plainText);
  }

  @Test
  void hashSameStringsExpectToBeEqual() {
    String plainText = "Hello";

    String result1 = instance.hash(plainText);
    String result2 = instance.hash(plainText);

    assertThat(result1)
        .isEqualTo(result2)
        .isNotEqualTo(plainText);
  }

  @Test
  void hashDifferentStringsExpectToBeNotEqual() {
    String plainText1 = "Hello";
    String plainText2 = "Bye";

    String result1 = instance.hash(plainText1);
    String result2 = instance.hash(plainText2);

    assertThat(result1).isNotEqualTo(result2);
  }

  @Test
  void matchWhenMatch() {
    String plainText = "Hello";
    String validHash = instance.hash(plainText);

    boolean result = instance.match(validHash, plainText);

    assertThat(result).isTrue();
  }

  @Test
  void matchWhenDoNotMatch() {
    String plainText = "Hello";
    String invalidHash = instance.hash("Bye");

    boolean result = instance.match(invalidHash, plainText);

    assertThat(result).isFalse();
  }
}
