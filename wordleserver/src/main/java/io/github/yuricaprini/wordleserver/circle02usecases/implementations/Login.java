package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleserver.circle01entities.Password;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.NoDigitPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.NoUppercasePasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.SpaceInPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.SpaceInUsernameException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooLongPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooLongUsernameException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooShortPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooShortUsernameException;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;

/**
 * A {@code Login} is a {@link RegisteredUserUseCase} characterized by the following narrative:
 * <ul>
 * <li>Description: RegisteredUser logs in to Wordle using username and password.
 * <li>Primary actor: RegisteredUser 
 * <li>Secondary actors: none
 * <li>Preconditions: RegisteredUser is not logged in.
 * <li>Main sequence of events: 
 *    <ul>
 *      <li> 1. RegisteredUser enters username and password in order to log in to Wordle.
 *      <li> 2. System validates the entered data.
 *      <li> 3. If validation is successful, the user is logged in, and an authentication token is 
 *              generated.
 *      <li> 4. System returns a successful login response with the generated authentication token.
 *    </ul>
 * <li>Postconditions: RegisteredUser is successfully logged in.
 * <li>Alternative sequences of events: 
 *    <ul>
 *      <li> 3.a RegisteredUser is not registered: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.b RegisteredUser provides invalid credentials: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.c RegisteredUser's username is too short: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.d RegisteredUser's username is too long: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.e RegisteredUser's username contains a space: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.f RegisteredUser's password is too short: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.g RegisteredUser's password is too long: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.h RegisteredUser's password contains a space: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.i RegisteredUser's password lacks a digit: RegisteredUser is not logged in, no 
 *               authentication token is created, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.j RegisteredUser's password lacks an uppercase letter: RegisteredUser is not logged
 *               in, no authentication token is created, System returns appropriate error response,
 *               and use case ends.
 *     </ul>
 * </ul>
 */
public class Login implements RegisteredUserUseCase {

  WordleResponse.Factory responseFactory;

  /**
   * Constructs a new Login use case with the provided response factory.
   *
   * @param responseFactory the factory for creating responses to the login attempt.
   */
  public Login(WordleResponse.Factory responseFactory) {
    this.responseFactory = responseFactory;
  }

  /**
   * Executes the Login use case narrative.
   *
   * @param request the request containing user credentials for login.
   * @return the response indicating the outcome of the login attempt.
   */
  @Override
  public WordleResponse execute(WordleRequest request) {

    try {
      Username username = new Username(request.getCredentialsDTO().getUsername());
      Password password = new Password(request.getCredentialsDTO().getPassword());
      User user = RegisteredUsers.getInstance().getBy(username);

      if (user == null || !user.getPassword().equals(password))
        return responseFactory.createLoginNO(
            user == null ? ErrorCode.NOT_REGISTERED_USER : ErrorCode.INVALID_CREDENTIALS);

      AuthToken authToken = AuthToken.newIstance(username.toString());

      return responseFactory.createLoginOK(authToken);

    } catch (TooShortUsernameException e) {
      return responseFactory.createLoginNO(ErrorCode.USERNAME_SHORT);
    } catch (TooLongUsernameException e) {
      return responseFactory.createLoginNO(ErrorCode.USERNAME_LONG);
    } catch (SpaceInUsernameException e) {
      return responseFactory.createLoginNO(ErrorCode.USERNAME_SPACE);
    } catch (TooShortPasswordException e) {
      return responseFactory.createLoginNO(ErrorCode.PASSWORD_SHORT);
    } catch (TooLongPasswordException e) {
      return responseFactory.createLoginNO(ErrorCode.PASSWORD_LONG);
    } catch (SpaceInPasswordException e) {
      return responseFactory.createLoginNO(ErrorCode.PASSWORD_SPACE);
    } catch (NoDigitPasswordException e) {
      return responseFactory.createLoginNO(ErrorCode.PASSWORD_NO_DIGIT);
    } catch (NoUppercasePasswordException e) {
      return responseFactory.createLoginNO(ErrorCode.PASSWORD_NO_UC);
    }
  }
}
