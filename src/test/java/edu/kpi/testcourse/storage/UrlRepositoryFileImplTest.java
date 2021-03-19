package edu.kpi.testcourse.storage;

import edu.kpi.testcourse.serialization.JsonToolJacksonImpl;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class UrlRepositoryFileImplTest {
  UrlShortenerConfig appConfig;
  UrlRepository urlRepository;

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



}
