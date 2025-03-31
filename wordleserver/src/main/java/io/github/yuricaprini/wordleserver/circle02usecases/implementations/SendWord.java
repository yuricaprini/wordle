package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import static io.github.yuricaprini.wordleserver.circle02usecases.implementations.EntityDTOMapper.*;
import java.util.ArrayList;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleserver.circle01entities.Clue;
import io.github.yuricaprini.wordleserver.circle01entities.Ranking;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.Score;
import io.github.yuricaprini.wordleserver.circle01entities.SecretWord;
import io.github.yuricaprini.wordleserver.circle01entities.Stats;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.Word;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.GameAlreadyPlayedException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.GameNotStartedException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.IllegalWordLengthException;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.SecretWordRepository;
import io.github.yuricaprini.wordleserver.circle02usecases.Top3Notifier;

public class SendWord implements RegisteredUserUseCase {

  WordleResponse.Factory responseFactory;
  SecretWordRepository secretWordRepository;
  Top3Notifier top3Notifier;


  public SendWord(WordleResponse.Factory responseFactory, SecretWordRepository secretWordRepository,
      Top3Notifier top3Notifier) {
    this.responseFactory = responseFactory;
    this.secretWordRepository = secretWordRepository;
    this.top3Notifier = top3Notifier;
  }

  @Override
  public WordleResponse execute(WordleRequest request) throws Exception {

    try {
      String userID = request.getAuthToken().validate();
      User user = RegisteredUsers.getInstance().getBy(new Username(userID));
      Word word = convertToWord(request.getWordDTO());

      if (!secretWordRepository.contains(word.getValue()))
        return responseFactory.createSendWordNO(ErrorCode.ILLEGAL_WORD_VOCABULARY);

      Triplet<Clue[], SecretWord, Stats> gameState = user.attemptsToGuess(word);

      Stats stats = gameState.getValue2();
      if (stats != null) { // means that game is over

        Score oldScore = stats.getOldScore();
        Score newScore = stats.getScore();

        ArrayList<Pair<Username, Score>> top3 =
            Ranking.getInstance().update(user.getUsername(), oldScore, newScore);

        if (top3 != null) // means that top3 has changed
          top3Notifier.notify(playersToDTOs(top3));
      }
      return responseFactory.createSendWordOK(gameStateToDTO(gameState));

    } catch (InvalidTokenException e) {
      return responseFactory.createSendWordNO(ErrorCode.INVALID_AUTHTOKEN);
    } catch (IllegalWordLengthException e) {
      return responseFactory.createSendWordNO(ErrorCode.ILLEGAL_WORD_LENGTH);
    } catch (GameAlreadyPlayedException e) {
      return responseFactory.createSendWordNO(ErrorCode.GAME_ALREADY_PLAYED);
    } catch (GameNotStartedException e) {
      return responseFactory.createSendWordNO(ErrorCode.GAME_NOT_STARTED);
    }
  }
}

