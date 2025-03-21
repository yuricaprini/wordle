package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import java.rmi.RemoteException;
import io.github.yuricaprini.wordleprotocol.remoteinterfaces.UserRegistrationRemoteService;
import io.github.yuricaprini.wordleserver.circle01entities.Password;
import io.github.yuricaprini.wordleserver.circle01entities.Ranking;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.Score;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.AlreadyRegisteredUserException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.NoDigitPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.NoUppercasePasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.SpaceInPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.SpaceInUsernameException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooLongPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooLongUsernameException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooShortPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooShortUsernameException;

/**
 * A {@code RegisterUser} is a Wordle use case characterized by the following narrative:
 * <ul>
 * <li>Description: NewUser registers to Wordle providing username and password.
 * <li>Primary actor: NewUser 
 * <li>Secondary actors: none
 * <li>Preconditions: NewUser is not registered to Wordle.
 * <li>Main sequence of events: 
 *    <ul>
 *      <li> 1. NewUser enters username and password.
 *      <li> 2. System validates the entered data.
 *      <li> 3. If validation is successful, a new user account is created.
 *      <li> 4. System returns a successful registration response.
 *    </ul>
 * <li>Postconditions: NewUser is successfully registered and can log in to Wordle.
 * <li>Alternative sequences of events: 
 *    <ul>
 *      <li> 3.a NewUser is already registered: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.b NewUser's username is too short: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.c NewUser's username is too long: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.d NewUser's username contains a space: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.e NewUser's password is too short: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.f NewUser's password is too long: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.g NewUser's password contains a space:System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.h NewUser's password lacks a digit: System returns appropriate error response, 
 *              no new user account is created, and use case ends.
 *      <li> 3.i NewUser's password lacks an uppercase letter: System returns appropriate error *            r response, no new user account is created, and use case ends.
 *     </ul>
 * </ul>
 * 
 * Executes its narrative implementing {@link UserRegistrationRemoteService}.
 */
public class RegisterUser implements UserRegistrationRemoteService {

  /**
   * {@inheritDoc}
   * <p>
   * Executes the registration use case narrative.
   */
  @Override
  public RegistrationOutcome registerUser(String username, String password) throws RemoteException {
    try {
      Username Username = new Username(username);

      RegisteredUsers.getInstance().add(new User(Username, new Password(password)));
      Ranking.getInstance().add(Username, new Score(0));

    } catch (AlreadyRegisteredUserException e) {
      return RegistrationOutcome.ALREADY_REGISTERED;
    } catch (TooShortUsernameException e) {
      return RegistrationOutcome.USERNAME_SHORT;
    } catch (TooLongUsernameException e) {
      return RegistrationOutcome.USERNAME_LONG;
    } catch (SpaceInUsernameException e) {
      return RegistrationOutcome.USERNAME_SPACE;
    } catch (TooShortPasswordException e) {
      return RegistrationOutcome.PASSWORD_SHORT;
    } catch (TooLongPasswordException e) {
      return RegistrationOutcome.PASSWORD_LONG;
    } catch (SpaceInPasswordException e) {
      return RegistrationOutcome.PASSWORD_SPACE;
    } catch (NoDigitPasswordException e) {
      return RegistrationOutcome.PASSWORD_NO_DIGIT;
    } catch (NoUppercasePasswordException e) {
      return RegistrationOutcome.PASSWORD_NO_UC;
    }
    return RegistrationOutcome.OK;
  }
}
