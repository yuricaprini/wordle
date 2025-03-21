package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import static io.github.yuricaprini.wordleserver.circle02usecases.implementations.EntityDTOMapper.cluesToDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleserver.circle01entities.Clue;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.GameAlreadyPlayedException;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;

/**
 * A {@code PlayWordle} is a {@link RegisteredUserUseCase} characterized by the following narrative:
 * <ul>
 * <li>Description: RegisteredUser requests to play a new Wordle game.
 * <li>Primary actor: RegisteredUser 
 * <li>Secondary actors: none
 * <li>Preconditions: RegisteredUser is logged in.
 * <li>Main sequence of events: 
 *    <ul>
 *      <li> 1. System checks that RegisteredUser is logged in.
 *      <li> 2. If this is true, checks that the RegisteredUser has not already played a game 
 *              for the current secret word.
 *      <li> 3. If this is true, a new game is set and System returns a successful response.
 *    </ul>
 * <li>Postconditions: RegisteredUser is ready to play a new Wordle game.
 * <li>Alternative sequences of events: 
 *    <ul>
 *      <li> 2.a RegisteredUser is not logged in, System returns appropriate error response, and 
 *               use case ends.
 *      <li> 3.a RegisteredUser has already played a full game and the secret word has not be 
 *               refreshed, System returns appropriate error response, and use case ends.
 *      <li> 3.b RegisteredUser has a game in progress, no new game is set, System returns a 
 *               successful response with the latest game clues (giving the user the opportunity to 
 *               complete it), and use case ends.
 * </ul>
 */
public class PlayWordle implements RegisteredUserUseCase {

  WordleResponse.Factory responseFactory;

  /**
   * Constructs a new PlayWordle use case with the provided response factory.
   *
   * @param responseFactory the factory for creating responses resulting from PlayWordle execution.
   */
  public PlayWordle(WordleResponse.Factory responseFactory) {
    this.responseFactory = responseFactory;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Executes the PlayWordle use case narrative.
   *
   * @param request the execution request of the PlayWordle narrative.
   * @return the response indicating the outcome of the PlayWordle narrative execution.
   */
  @Override
  public WordleResponse execute(WordleRequest request) {

    try {

      String userID = request.getAuthToken().validate();
      User user = RegisteredUsers.getInstance().getBy(new Username(userID));

      Clue[] clues = user.requestsNewGame();

      return responseFactory.createPlayWordleOK(cluesToDTO(clues));

    } catch (InvalidTokenException e) {
      return responseFactory.createPlayWordleNO(ErrorCode.INVALID_AUTHTOKEN);
    } catch (GameAlreadyPlayedException e) {
      return responseFactory.createPlayWordleNO(ErrorCode.GAME_ALREADY_PLAYED);
    }
  }
}
