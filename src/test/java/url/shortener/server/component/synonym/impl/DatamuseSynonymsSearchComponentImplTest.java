package url.shortener.server.component.synonym.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.http.client.RxHttpClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatamuseSynonymsSearchComponentImplTest {

  @Mock
  private RxHttpClient rxHttpClient;

  @InjectMocks
  private DatamuseSynonymsSearchComponentImpl instance;

  private static Object[][] isSearchableTestDataProvider() {
    return new Object[][]{
        {"123", false},
        {"hello1", false},
        {"Hello world", false},
        {"car", true},
        {"aaa", true},
        {"Hello", true},
    };
  }

  @ParameterizedTest
  @MethodSource("isSearchableTestDataProvider")
  void isSearchable(String value, boolean expected) {
    boolean result = instance.isSearchable(value);

    assertThat(result).isEqualTo(expected);
  }
}
