package io.github.yuricaprini.wordleprotocol.remoteinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A {@code UserRegistrationRemoteService} is the {@link Remote} interface responsible for defining 
 * the essential methods required for user registration to Wordle.
 */
public interface UserRegistrationRemoteService extends Remote {

  /**
  * Registers a new user to Wordle.
  * 
  * @param username the username required for user registration to Wordle.
  * @param password the password required for user registration to Wordle.
  * @return the outcome code of the user registration attempt, which can be one of the following:
  *         <ul>
  *           <li>{@link RegistrationOutcome#OK} if registration successfull</li>
  *           <li>{@link RegistrationOutcome#USERNAME_SHORT} if {@code username.length()<4} </li>
  *           <li>{@link RegistrationOutcome#USERNAME_LONG} if {@code username.length()>10}</li>
  *           <li>{@link RegistrationOutcome#USERNAME_SPACE} if {@code username.contains(" ")}</li>
  *           <li>{@link RegistrationOutcome#PASSWORD_SHORT} if {@code password.length()<8}</li>
  *           <li>{@link RegistrationOutcome#PASSWORD_LONG} if {@code password.length()>16}</li>
  *           <li>{@link RegistrationOutcome#PASSWORD_SPACE} if {@code password.contains(" ")}</li>
  *           <li>{@link RegistrationOutcome#PASSWORD_NO_DIGIT} if password has no digit</li>
  *           <li>{@link RegistrationOutcome#PASSWORD_NO_UC} if password has no uppercase</li>
  *           <li>{@link RegistrationOutcome#ALREADY_REGISTERED} if username already registered</li>
  *         </ul>
  * @throws NullPointerException if {@code username == null} or {@code password == null}
  * @throws RemoteException if a communication error occurs during the execution of the remote call
  */
  public RegistrationOutcome registerUser(String username, String password) throws RemoteException;

  /**
  * A {@code RegistrationOutcome} represents the possible outcomes of a user registration attempt.
  */
  public enum RegistrationOutcome {

    OK, USERNAME_SHORT, USERNAME_LONG, USERNAME_SPACE, PASSWORD_SHORT, PASSWORD_LONG, PASSWORD_SPACE, PASSWORD_NO_DIGIT, PASSWORD_NO_UC, ALREADY_REGISTERED

  }

  /**
  * An abstract factory for creating instances of {@code UserRegistrationRemoteService}.
  */
  public interface Factory {
    public UserRegistrationRemoteService createUserRegistrationRemoteService();
  }
}
