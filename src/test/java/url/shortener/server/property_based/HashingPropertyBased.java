package url.shortener.server.property_based;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import url.shortener.server.component.encryption.impl.HashingComponentDummyImpl;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HashingPropertyBased {

  // GIVEN
  @InjectMocks
  private HashingComponentDummyImpl hashingEngineInstance;

  @Test
  void expectEqualHashesFromSameString() {
    qt()
      .forAll(
        strings().basicLatinAlphabet().ofLengthBetween(0, 10)
      ).check((unhashedText) -> {
      // WHEN
      String hashedText1 = hashingEngineInstance.hash(unhashedText);
      String hashedText2 = hashingEngineInstance.hash(unhashedText);

      // THEN
      assertThat(hashedText1).isEqualTo(hashedText2);
    });
  }

}
