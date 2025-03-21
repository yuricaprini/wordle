package io.github.yuricaprini.wordleserver.circle01entities.exceptions;

/**
 * A {@code TooShortPasswordException} is thrown when a password does not meet the minimum length requirement.
 */
public class TooShortPasswordException extends Exception {

  /**
   * Constructs a new {@code TooShortPasswordException} with a default error message.
   */
  public TooShortPasswordException() {
    super("Password is too short.");
  }
}
