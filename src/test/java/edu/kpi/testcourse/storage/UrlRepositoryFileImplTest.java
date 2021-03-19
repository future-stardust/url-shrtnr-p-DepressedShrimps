package edu.kpi.testcourse.storage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import edu.kpi.testcourse.serialization.JsonToolJacksonImpl;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UrlRepositoryFileImplTest {
  UrlShortenerConfig appConfig;
  UrlRepository urlRepository;

  // GIVEN
  @BeforeEach
  void setUp() {
    try {
      appConfig = new UrlShortenerConfig(
        Files.createTempDirectory("url-repository-file-test"));
      Files.write(appConfig.storageRoot().resolve("url-repository.json"), "{}".getBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    urlRepository = new UrlRepositoryFileImpl(new JsonToolJacksonImpl(), appConfig);
  }

  @AfterEach
  void tearDown() {
    try {
      Files.delete(appConfig.storageRoot().resolve("url-repository.json"));
      Files.delete(appConfig.storageRoot());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldCreateAlias() {
    // WHEN
    UrlAlias alias = new UrlAlias("http://r.com/short", "http://g.com/long", "aaa@bbb.com");
    urlRepository.createUrlAlias(alias);

    // THEN
    assertThat(urlRepository.findUrlAlias("http://r.com/short")).isEqualTo(alias);
  }

  @Test
  void shouldNotAllowToCreateSameAliases() {
    // WHEN
    UrlAlias alias1 = new UrlAlias("http://r.com/short", "http://g.com/long1", "aaa@bbb.com");
    urlRepository.createUrlAlias(alias1);

    // THEN
    UrlAlias alias2 = new UrlAlias("http://r.com/short", "http://g.com/long2", "aaa@bbb.com");
    assertThatThrownBy(() -> {
      urlRepository.createUrlAlias(alias2);
    }).isInstanceOf(UrlRepository.AliasAlreadyExist.class);
  }

  @Test
  void shouldDeleteAlias() {
    // WHEN
    UrlAlias alias = new UrlAlias("http://r.com/short", "http://g.com/long", "aaa@bbb.com");
    urlRepository.createUrlAlias(alias);
    urlRepository.deleteUrlAlias("aaa@bbb.com", "http://r.com/short");

    // THEN
    UrlAlias alias = urlRepository.findUrlAlias("http://r.com/short");
    assertThat(urlRepository.findUrlAlias("http://r.com/short")).isEqualTo(null);
  }

}
