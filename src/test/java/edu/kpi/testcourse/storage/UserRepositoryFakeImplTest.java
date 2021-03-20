package edu.kpi.testcourse.storage;

import edu.kpi.testcourse.entities.User;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryFakeImplTest {

  @Test
  void createsOneUser() {
    // GIVEN
    var userRepository = new UserRepositoryFakeImpl();
    var email = "user1@example.org";
    var user = new User(email, "hash1", new ArrayList<String>());

    // WHEN
    userRepository.createUser(user);

    // THEN
    assertThat(userRepository.findUser(email)).isEqualTo(user);
  }

  @Test
  void doesNotCreateDuplicateUser() {
    // GIVEN
    var userRepository = new UserRepositoryFakeImpl();
    var email = "user1@example.org";
    var user = new User(email, "hash1", new ArrayList<String>());
    userRepository.createUser(user);

    // WHEN + THEN
    assertThrows(RuntimeException.class, () -> userRepository.createUser(user));
  }

  @Test
  void findsCorrectUser() {
    // GIVEN
    var userRepository = new UserRepositoryFakeImpl();
    var email1 = "user1@example.org";
    var user1 = new User(email1, "hash1", new ArrayList<String>());
    var email2 = "user2@example.org";
    var user2 = new User(email2, "hash2", new ArrayList<String>());

    // WHEN
    userRepository.createUser(user1);
    userRepository.createUser(user2);

    // THEN
    assertThat(userRepository.findUser(email1)).isEqualTo(user1);
    assertThat(userRepository.findUser(email2)).isEqualTo(user2);
  }

  @Test
  void doesNotFindNonexistentUser() {
    // GIVEN + WHEN
    var userRepository = new UserRepositoryFakeImpl();

    // THEN
    assertThat(userRepository.findUser("user1@example.org")).isEqualTo(null);
  }

  @Test
  void addUserAliasTestAndListAliasesTest () {
    // GIVEN
    var userRepository = new UserRepositoryFakeImpl();
    var email = "user@example.org";
    var user = new User(email, "hash1", new ArrayList<String>());

    userRepository.createUser(user);

    // WHEN
    userRepository.addUrlAlias(email, "short");

    // THEN
    assertThat(userRepository.getAllAliasesForUser(email).get(0)).isEqualTo("short");
  }

  @Test
  void deleteUserAliasTest () {
    // GIVEN
    var userRepository = new UserRepositoryFakeImpl();
    var email = "user@example.org";
    var user = new User(email, "hash1", new ArrayList<String>());

    userRepository.createUser(user);
    userRepository.addUrlAlias(email, "short");

    // WHEN
    userRepository.deleteUrlAlias(email, "short");

    // THEN
    assertThat(userRepository.getAllAliasesForUser(email)).isEmpty();
  }
}
