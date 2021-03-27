package url.shortener.server.bigtable;

import java.util.Optional;
import javax.validation.constraints.NotNull;

public interface BigTable {

  /**
   * @return {@code true} if given key is present is database otherwise {@code false} is returned
   */
  boolean containsKey(@NotNull String key);

  /**
   * @return {@code true} if key-value was successfully written to database otherwise {@code false}
   * is returned
   */
  boolean put(@NotNull String key, @NotNull String value);

  /**
   * @return {@code true} if key was present and successfully removed, otherwise {@code false} is
   * returned
   */
  boolean deleteByKey(@NotNull String key);

  Optional<String> findByKey(@NotNull String key);

}
