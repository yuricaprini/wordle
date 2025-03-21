package io.github.yuricaprini.wordleserver.unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleserver.circle01entities.Password;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;

class UserTest {

  @Test
  void constructorShouldThrowExceptionIfUsernameIsNull() {
    assertThrows(NullPointerException.class, () -> new User(null, new Password("Password1")));
  }

  @Test
  void constructorShouldThrowExceptionIfPasswordIsNull() {
    assertThrows(NullPointerException.class, () -> new User(new Username("username"), null));
  }

  @Test
  void getUsernameShouldReturnCorrectUsername() throws Exception {
    Username username = new Username("username");
    Password password = new Password("Password1");
    User user = new User(username, password);

    assertEquals(username, user.getUsername());
  }

  @Test
  void getPasswordShouldReturnCorrectPassword() throws Exception {
    Username username = new Username("username");
    Password password = new Password("Password1");
    User user = new User(username, password);

    assertEquals(password, user.getPassword());
  }

  @Test
  void equalsShouldReturnTrueForEqualUsers() throws Exception {
    Username username1 = new Username("username1");
    Password password1 = new Password("Password1!");
    User user1 = new User(username1, password1);

    Username username2 = new Username("username1");
    Password password2 = new Password("Password1!");
    User user2 = new User(username2, password2);

    assertTrue(user1.equals(user2));
    assertTrue(user1.equals(user1));
  }

  @Test
  void equalsShouldReturnFalseForDifferentUsers() throws Exception {
    new Username("username1");
    new Password("Password1!");
    User user1 = new User(new Username("username1"), new Password("Password1!"));
    User user2 = new User(new Username("username2"), new Password("Password1!"));

    assertFalse(user1.equals(user2));
  }

  @Test
  void hashCodeShouldBeEqualForEqualUsers() throws Exception {
    Username username1 = new Username("username");
    Password password1 = new Password("Password1");
    User user1 = new User(username1, password1);

    Username username2 = new Username("username");
    Password password2 = new Password("Password1");
    User user2 = new User(username2, password2);

    assertEquals(user1.hashCode(), user2.hashCode());
  }

  @Test
  void hashCodeShouldBeDifferentForDifferentUsers() throws Exception {
    User user1 = new User(new Username("username"), new Password("Password1"));
    User user2 = new User(new Username("username"), new Password("Password2"));

    assertNotEquals(user1.hashCode(), user2.hashCode());
  }

  @Test
  void toStringShouldReturnStringValue() throws Exception {
    Username username = new Username("username");
    Password password = new Password("Password1");
    User user = new User(username, password);

    String expectedString =
        "User{username=" + username.toString() + ", password=" + password.toString() + '}';
    assertEquals(expectedString, user.toString());
  }
}

