package edu.kpi.testcourse.storage;

import edu.kpi.testcourse.entities.UrlAlias;
import edu.kpi.testcourse.entities.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * An in-memory fake implementation of {@link UserRepository}.
 */
public class UserRepositoryFakeImpl implements UserRepository {
  private final Map<String, User> users = new HashMap<>();

  @Override
  public void createUser(User user) {
    if (users.putIfAbsent(user.email(), user) != null) {
      throw new RuntimeException("User already exists");
    }
  }

  @Override
  public @Nullable User findUser(String email) {
    return users.get(email);
  }


  @Override
  public List<String> getAllAliasesForUser(String email) {
    return users.get(email).urls();
  }

  @Override
  public void addUrlAlias(String email, String alias) {
    if (users.containsKey(email)){
      users.get(email).urls().add(alias);
    }
  }

  @Override
  public void deleteUrlAlias(String email, String alias) {
    User user = users.get(email);
    user.urls().remove(alias);
  }
}
