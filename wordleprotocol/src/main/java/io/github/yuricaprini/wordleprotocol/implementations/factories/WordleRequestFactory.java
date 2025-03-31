package io.github.yuricaprini.wordleprotocol.implementations.factories;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;
import io.github.yuricaprini.wordleprotocol.implementations.WordleHttpRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest.Type;

/**
 * An implementation of {@link WordleRequest.Factory} using {@link WordleHttpRequest} istances.
 */
public class WordleRequestFactory implements WordleRequest.Factory {

  @Override
  public WordleRequest createEmptyRequest() {
    return new WordleHttpRequest.Builder().withFullyPopulated(false).build();
  }

  @Override
  public WordleRequest createLoginRequest(CredentialsDTO credentials) {
    return new WordleHttpRequest.Builder().withType(Type.LOGIN).withCredentials(credentials)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleRequest createPlayWordleRequest(AuthToken token) {
    return new WordleHttpRequest.Builder().withType(Type.PLAY_WORDLE).withAuthToken(token)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleRequest createSendWordRequest(AuthToken token, WordDTO word) {
    return new WordleHttpRequest.Builder().withType(Type.SEND_WORD).withAuthToken(token)
        .withWord(word).withFullyPopulated(true).build();
  }

  @Override
  public WordleRequest createShowMeStatsRequest(AuthToken token) {
    return new WordleHttpRequest.Builder().withType(Type.SHOWME_STATS).withAuthToken(token)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleRequest createShowMeRankingRequest(AuthToken token, PlayerDTO player) {
    return new WordleHttpRequest.Builder().withType(Type.SHOWME_RANKING).withAuthToken(token)
        .withPlayer(player).withFullyPopulated(true).build();
  }

  @Override
  public WordleRequest createShareRequest(AuthToken token) {
    return new WordleHttpRequest.Builder().withType(Type.SHARE).withAuthToken(token)
        .withFullyPopulated(true).build();
  }
}
