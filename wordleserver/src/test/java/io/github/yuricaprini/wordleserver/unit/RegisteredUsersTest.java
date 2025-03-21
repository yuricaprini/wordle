package io.github.yuricaprini.wordleserver.unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleserver.circle01entities.Password;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.AlreadyRegisteredUserException;

class RegisteredUsersTest {

  @Test
  void getInstanceShouldBeOK() {
    RegisteredUsers instance1 = RegisteredUsers.getInstance();
    RegisteredUsers instance2 = RegisteredUsers.getInstance();

    assertSame(instance1, instance2);
  }

  @Test
  void addShouldThrowsExceptionIfUserNull() {
    assertThrows(NullPointerException.class, () -> RegisteredUsers.getInstance().add(null));
  }

  @Test
  void addShouldThrowsExceptionIfAlreadyRegistered() throws Exception {
    RegisteredUsers registeredUsers = RegisteredUsers.getInstance();
    Username username = new Username("username1");
    Password password = new Password("Password1");
    User user1 = new User(username, password);
    registeredUsers.add(user1);

    User user2 = new User(username, new Password("Password2"));

    assertThrows(AlreadyRegisteredUserException.class,
        () -> RegisteredUsers.getInstance().add(user2));
  }

  @Test
  void addShouldBeOk() throws Exception {
    RegisteredUsers registeredUsers = RegisteredUsers.getInstance();
    Username username = new Username("username20");
    Password password = new Password("Password2");
    User user = new User(username, password);
    registeredUsers.add(user);

    assertTrue(RegisteredUsers.getInstance().getBy(username).equals(user));
  }

  @Test
  void getByShouldThrowExceptionIfUsernameIsNull() {
    assertThrows(NullPointerException.class, () -> RegisteredUsers.getInstance().getBy(null));
  }

  @Test
  void getByShouldBeOK() throws Exception {
    assertNull(RegisteredUsers.getInstance().getBy(new Username("notexists")));
  }
}
