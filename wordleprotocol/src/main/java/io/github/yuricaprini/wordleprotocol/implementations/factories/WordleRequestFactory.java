package io.github.yuricaprini.wordleprotocol.implementations.factories;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
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
  public WordleRequest createLogoutRequest(AuthToken token) {
    return new WordleHttpRequest.Builder().withType(Type.LOGOUT).withAuthToken(token)
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
  public WordleRequest createSendMeStatisticsRequest(AuthToken token) {
    return new WordleHttpRequest.Builder().withType(Type.SENDME_STATS).withAuthToken(token)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleRequest createShareRequest(AuthToken token) {
    return new WordleHttpRequest.Builder().withType(Type.SHARE).withAuthToken(token)
        .withFullyPopulated(true).build();
  }
}
