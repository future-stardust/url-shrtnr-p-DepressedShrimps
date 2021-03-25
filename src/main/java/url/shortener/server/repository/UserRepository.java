package url.shortener.server.repository;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import url.shortener.server.entity.User;

public interface UserRepository {

  Optional<User> findById(@NotNull String id);

  boolean existsById(@NotNull String id);

  void deleteById(@NotNull String id);

  boolean save(@NotNull User object);

}
