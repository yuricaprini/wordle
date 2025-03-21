package io.github.yuricaprini.wordleserver.unit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleserver.circle01entities.Password;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.*;

class PasswordTest {

  @Test
  void constructorShouldThrowExceptionIfValueIsNull() {
    assertThrows(NullPointerException.class, () -> new Password(null));
  }

  @Test
  void constructorShouldThrowExceptionIfLengthIsLessThanMin() {
    assertThrows(TooShortPasswordException.class, () -> new Password("It's<8!"));
  }

  @Test
  void constructorShouldThrowExceptionIfLengthIsGreaterThanMax() {
    assertThrows(TooLongPasswordException.class, () -> new Password("It'sGreaterThan16"));
  }

  @Test
  void constructorShouldThrowExceptionIfContainsSpace() {
    assertThrows(SpaceInPasswordException.class, () -> new Password("Contain 1space!"));
  }

  @Test
  void constructorShouldThrowExceptionIfNoDigit() {
    assertThrows(NoDigitPasswordException.class, () -> new Password("NoDigit!"));
  }

  @Test
  void constructorShouldThrowExceptionIfNoUppercase() {
    assertThrows(NoUppercasePasswordException.class, () -> new Password("nouppercase1"));
  }

  @Test
  void constructorShouldBeOk() throws Exception {
    String validPassword = "Password1";
    Password password = new Password(validPassword);

    assertEquals(validPassword, password.toString());
  }

  @Test
  void equalsShouldReturnTrueForEqualPasswords() throws Exception {
    String passwordStr = "Password1";
    Password password1 = new Password(passwordStr);
    Password password2 = new Password(passwordStr);

    assertTrue(password1.equals(password2));
    assertTrue(password1.equals(password1));
  }

  @Test
  void equalsShouldReturnFalseForDifferentPasswords() throws Exception {
    Password password1 = new Password("Password1!");
    Password password2 = new Password("Password2!");

    assertFalse(password1.equals(password2));
  }

  @Test
  void hashCodeShouldBeEqualForEqualPasswords() throws Exception {
    String passwordStr = "Password1!";
    Password password1 = new Password(passwordStr);
    Password password2 = new Password(passwordStr);

    assertEquals(password1.hashCode(), password2.hashCode());
  }

  @Test
  void hashCodeShouldBeDifferentForDifferentPasswords() throws Exception {
    Password password1 = new Password("Password1!");
    Password password2 = new Password("Password2!");

    assertNotEquals(password1.hashCode(), password2.hashCode());
  }

  @Test
  void toStringShouldReturnStringValue() throws Exception {
    String passwordStr = "Password1!";
    Password password = new Password(passwordStr);

    assertEquals(passwordStr, password.toString());
  }
}
