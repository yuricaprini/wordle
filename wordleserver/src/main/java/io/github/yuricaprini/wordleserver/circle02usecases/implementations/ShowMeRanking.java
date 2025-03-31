package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import static io.github.yuricaprini.wordleserver.circle02usecases.implementations.EntityDTOMapper.playersToDTOs;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleserver.circle01entities.Ranking;
import io.github.yuricaprini.wordleserver.circle01entities.Score;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;

public class ShowMeRanking implements RegisteredUserUseCase {

  private WordleResponse.Factory responseFactory;
  private final int PAGEDIM = 10;

  public ShowMeRanking(WordleResponse.Factory responseFactory) {
    this.responseFactory = responseFactory;
  }

  @Override
  public WordleResponse execute(WordleRequest request) {
    try {
      request.getAuthToken().validate();
      PlayerDTO playerDTO = request.getPlayerDTO();
      if (playerDTO == null) {
        return responseFactory
            .createShowMeRankingOK(playersToDTOs(Ranking.getInstance().getBottomUp(PAGEDIM)));
      } else
        return responseFactory.createShowMeRankingOK(
            playersToDTOs(Ranking.getInstance().getBottomUp(new Username(playerDTO.getName()),
                new Score(playerDTO.getScore()), PAGEDIM)));

    } catch (InvalidTokenException e) {
      return responseFactory.createShowMeRankingNO(ErrorCode.INVALID_AUTHTOKEN);
    } catch (Exception e) {
      return responseFactory.createShowMeRankingNO(ErrorCode.INVALID_CURSOR);
    }
  }

}
