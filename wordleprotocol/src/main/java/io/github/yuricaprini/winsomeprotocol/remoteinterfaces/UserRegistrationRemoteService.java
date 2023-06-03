package io.github.yuricaprini.winsomeprotocol.remoteinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import io.github.yuricaprini.winsomeprotocol.exceptions.CannotRegisterUserException;

/**
 * A <code>UserRegistrationRemoteService</code> is the RMI remote interface responsible for
 * declaring the essential methods required for user registration in the Wordle application.
 */
public interface UserRegistrationRemoteService extends Remote {

  /**
   * Registers a new user to Wordle.
   * 
   * @param username the username required for user registration to Wordle
   * @param password the password required for user registration to Wordle
   * @return <code>true</code> if the registration was successful, <code>false</code> otherwise
   * @throws NullPointerException if <code> username==null || password==null
   * @throws CannotRegisterUserException if <code> username.length()<6 || username.length()>8 || 
   *         username.contains(" ") || password.length()<8 || password.length()>16 || 
   *         password.contains(" ") || password has no uppercase || password has no digit || 
   *          </code> a user with the same username is already registered to Wordle
   * @throws RemoteException if a communication error occurs during the execution of the remote call
   */
  public RegisterUserOutcomeCode registerUser(String username, String password, String[] tags)
      throws RemoteException;

  /**
   * An abstract factory for <code>UserRegistrationRemoteService</code>.
   */
  public interface Factory {
    public UserRegistrationRemoteService createUserRegistrationRemoteService();
  }

  public enum RegisterUserOutcomeCode {

    REGISTRATION_OK, USERNAME_TOO_SHORT, USERNAME_TOO_LONG, USERNAME_CONTAINS_WHITESPACE, PASSWORD_TOO_SHORT, PASSWORD_TOO_LONG, PASSWORD_CONTAINS_WHITESPACE, PASSWORD_NO_DIGIT, PASSWORD_NO_UPPERCASE, ALREADY_REGISTERED_USER

  }

}
