package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import static io.github.yuricaprini.wordleserver.circle02usecases.implementations.EntityDTOMapper.*;

import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;

public class ShowMeStats implements RegisteredUserUseCase {

  private WordleResponse.Factory responseFactory;

  public ShowMeStats(WordleResponse.Factory responseFactory) {
    this.responseFactory = responseFactory;
  }

  @Override
  public WordleResponse execute(WordleRequest request) {
    try {
      String userID = request.getAuthToken().validate();
      User user = RegisteredUsers.getInstance().getBy(new Username(userID));

      return responseFactory.createShowMeStatsOK(statsToDTO(user.getStats()));

    } catch (InvalidTokenException e) {
      return responseFactory.createShowMeStatsNO(ErrorCode.INVALID_AUTHTOKEN);
    }
  }

}
