package url.shortener.server.component.encryption.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashingComponentDummyImplTest {

  @InjectMocks
  private HashingComponentDummyImpl instance;

  @Test
  void hashExpectSameValue() {
    String plainText = "Hello";

    String result = instance.hash(plainText);

    assertThat(result)
        .isNotBlank()
        .isEqualTo(plainText);
  }

  @Test
  void hashExpectedTwoSameHashedValueToBeEqual() {
    String plainText = "Hello";

    String result1 = instance.hash(plainText);
    String result2 = instance.hash(plainText);

    assertThat(result1)
        .isEqualTo(result2)
        .isEqualTo(plainText);
  }

  @Test
  void matchExpectSameValueBeEqual() {
    String plainText = "Hello";
    String validHash = instance.hash(plainText);

    boolean result = instance.match(validHash, plainText);

    assertThat(result).isTrue();
  }

  @Test
  void matchExpectDifferentValueToBeNotEqual() {
    String plainText = "Hello";
    String invalidHash = instance.hash("Bye");

    boolean result = instance.match(invalidHash, plainText);

    assertThat(result).isFalse();
  }
}
