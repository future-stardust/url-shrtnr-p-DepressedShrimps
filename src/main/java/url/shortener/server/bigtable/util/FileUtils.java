package url.shortener.server.bigtable.util;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNullElseGet;

import java.io.File;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class FileUtils {

  private static final String DATABASE_NAME = "BigTable";

  public static File createWorkingRootDirectory(String workingRootDirectory, String tableName) {
    String dir = requireNonNullElseGet(workingRootDirectory, () -> System.getProperty("user.dir"))
        + File.separator
        + DATABASE_NAME
        + File.separator
        + tableName;
    File rootDir = new File(dir);

    if (!rootDir.exists()) {
      boolean dirCreated = rootDir.mkdirs();
      if (!dirCreated) {
        log.error(
            "::DB:: cannot create root dir by path '{}'. Please, check if path is valid", rootDir);
        throw new IllegalArgumentException(
            format("Cannot create directory by this path {0} ", rootDir)
        );
      }
    }
    return rootDir;
  }

}
