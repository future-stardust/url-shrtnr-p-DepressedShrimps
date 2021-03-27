package url.shortener.server.bigtable;

import java.util.List;
import javax.validation.constraints.NotNull;

public interface MultiValueTable {

  /**
   * @return {@code true} if key-value was successfully written to database otherwise {@code false}
   * is returned
   */
  boolean put(@NotNull String key, @NotNull String value);

  /**
   * @return {@code true} if pair was present and successfully removed, otherwise {@code false} is
   * returned
   */
  boolean deleteByKey(@NotNull String key, @NotNull String value);

  List<String> findByKey(@NotNull String key);

}
