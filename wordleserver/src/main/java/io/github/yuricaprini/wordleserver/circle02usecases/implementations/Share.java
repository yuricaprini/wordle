package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import static io.github.yuricaprini.wordleserver.circle02usecases.implementations.EntityDTOMapper.gameResultToDTO;
import org.javatuples.Pair;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleserver.circle01entities.Clue;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.SecretWord;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle02usecases.GameResultSharer;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;

public class Share implements RegisteredUserUseCase {

  private GameResultSharer gameResultSharer;
  private WordleResponse.Factory responseFactory;

  public Share(GameResultSharer gameResultSharer, WordleResponse.Factory responseFactory) {
    this.gameResultSharer = gameResultSharer;
    this.responseFactory = responseFactory;
  }

  @Override
  public WordleResponse execute(WordleRequest request) {

    try {
      String userID = request.getAuthToken().validate();
      Username username = new Username(userID);
      User user = RegisteredUsers.getInstance().getBy(username);

      Pair<SecretWord, Clue[]> result = user.getGameResult();
      if (result != null) {
        try {
          gameResultSharer.share(gameResultToDTO(username, result.getValue0(), result.getValue1()));
        } catch (Exception e) {
          return responseFactory.createShareNO(ErrorCode.INTERNAL_ERROR);
        }
      } else
        return responseFactory.createShareNO(ErrorCode.NO_GAMES_PLAYED);

      return responseFactory.createShareOK();

    } catch (InvalidTokenException e) {
      return responseFactory.createShareNO(ErrorCode.INVALID_AUTHTOKEN);
    }
  }
}
