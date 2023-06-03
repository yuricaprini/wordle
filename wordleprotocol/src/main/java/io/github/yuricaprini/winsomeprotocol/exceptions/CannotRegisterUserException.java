package io.github.yuricaprini.winsomeprotocol.exceptions;

/**
 * A <code>CannotRegisterUserException</code> is a checked exception thrown when trying to register
 * an already registered user or a user with an illegal username, password or tags.
 */
public class CannotRegisterUserException extends Exception{

  public CannotRegisterUserException(String message) {
    super(message);
  }

  public CannotRegisterUserException(Throwable cause) {
    super(cause);
  }

  public CannotRegisterUserException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
