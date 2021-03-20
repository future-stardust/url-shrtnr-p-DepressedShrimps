package edu.kpi.testcourse.storage;

import com.google.gson.reflect.TypeToken;
import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.logic.UrlShortenerConfig;
import edu.kpi.testcourse.serialization.JsonTool;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * A file-backed implementation of {@link UrlRepository} suitable for use in production.
 */
public class UrlRepositoryFileImpl implements UrlRepository {
  private final Map<String, UrlAlias> aliases;

  private final JsonTool jsonTool;
  private final UrlShortenerConfig appConfig;

  /**
   * Creates an instance.
   */
  @Inject
  public UrlRepositoryFileImpl(
      JsonTool jsonTool,
      UrlShortenerConfig appConfig
  ) {
    this.jsonTool = jsonTool;
    this.appConfig = appConfig;
    this.aliases = readUrlsFromJsonDatabaseFile(
        jsonTool,
        makeJsonFilePath(appConfig.storageRoot()
        ));
  }

  @Override
  public synchronized void createUrlAlias(UrlAlias urlAlias) {
    if (aliases.putIfAbsent(urlAlias.alias(), urlAlias) != null) {
      throw new RuntimeException("Url already exists");
    }
    writeUrlsToJsonDatabaseFile(jsonTool, aliases, makeJsonFilePath(appConfig.storageRoot()));
  }

  @Override
  public @Nullable UrlAlias findUrlAlias(String alias) {
    return aliases.get(alias);
  }

  @Override
  public void deleteUrlAlias(String email, String alias) {
    if (aliases.containsKey(alias)) {
      aliases.remove(alias);
      writeUrlsToJsonDatabaseFile(jsonTool, aliases, makeJsonFilePath(appConfig.storageRoot()));
    }
  }

  private static Path makeJsonFilePath(Path storageRoot) {
    return storageRoot.resolve("url-repository.json");
  }

  private static Map<String, UrlAlias> readUrlsFromJsonDatabaseFile(
      JsonTool jsonTool, Path sourceFilePath
  ) {
    String json;
    try {
      json = Files.readString(sourceFilePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Type type = new TypeToken<HashMap<String, UrlAlias>>(){}.getType();
    Map<String, UrlAlias> result = jsonTool.fromJson(json, type);
    if (result == null) {
      throw new RuntimeException("Could not deserialize the user repository");
    }
    return result;
  }

  private static void writeUrlsToJsonDatabaseFile(
      JsonTool jsonTool, Map<String, UrlAlias> urls, Path destinationFilePath
  ) {
    String json = jsonTool.toJson(urls);
    try {
      Files.write(destinationFilePath, json.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
