package io.github.yuricaprini.wordleserver.unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.*;

class UsernameTest {

  @Test
  void constructorShouldThrowExceptionIfValueIsNull() {
    assertThrows(NullPointerException.class, () -> new Username(null));
  }

  @Test
  void constructorShouldThrowExceptionIfLengthIsLessThanMin() {
    assertThrows(TooShortUsernameException.class, () -> new Username("<4!"));
  }

  @Test
  void constructorShouldThrowExceptionIfLengthIsGreaterThanMax() {
    assertThrows(TooLongUsernameException.class, () -> new Username("It's>10!!!!"));
  }

  @Test
  void constructorShouldThrowExceptionIfContainsSpace() {
    assertThrows(SpaceInUsernameException.class, () -> new Username("Contain s"));
  }

  @Test
  void constructorShouldBeOk() throws Exception {
    String validUsername = "username";
    Username username = new Username(validUsername);

    assertEquals(validUsername, username.toString());
  }

  @Test
  void equalsShouldReturnTrueForEqualUsernames() throws Exception {
    String usernameStr = "User123";
    Username username1 = new Username(usernameStr);
    Username username2 = new Username(usernameStr);

    assertTrue(username1.equals(username2));
    assertTrue(username1.equals(username1));
  }

  @Test
  void equalsShouldReturnFalseForDifferentUsernames() throws Exception {
    Username username1 = new Username("User123");
    Username username2 = new Username("User456");

    assertFalse(username1.equals(username2));
  }

  @Test
  void hashCodeShouldBeEqualForEqualUsernames() throws Exception {
    String usernameStr = "User123";
    Username username1 = new Username(usernameStr);
    Username username2 = new Username(usernameStr);

    assertEquals(username1.hashCode(), username2.hashCode());
  }

  @Test
  void hashCodeShouldBeDifferentForDifferentUsernames() throws Exception {
    Username username1 = new Username("User123");
    Username username2 = new Username("User456");

    assertNotEquals(username1.hashCode(), username2.hashCode());
  }

  @Test
  void toStringShouldReturnStringValue() throws Exception {
    String usernameStr = "User123";
    Username username = new Username(usernameStr);

    assertEquals(usernameStr, username.toString());
  }
}
