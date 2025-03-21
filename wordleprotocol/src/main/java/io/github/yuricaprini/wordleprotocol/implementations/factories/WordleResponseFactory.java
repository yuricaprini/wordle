package io.github.yuricaprini.wordleprotocol.implementations.factories;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.implementations.WordleHttpResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.Type;

/**
 * An implementation of {@link WordleResponse.Factory} using {@link WordleHttpResponse} istances.
 */
public class WordleResponseFactory implements WordleResponse.Factory {

  @Override
  public WordleResponse createEmptyResponse() {
    return new WordleHttpResponse.Builder().withFullyPopulated(false).build();
  }

  @Override
  public WordleResponse createLoginOK(AuthToken authToken) {
    return new WordleHttpResponse.Builder().withType(Type.LOGIN_OK).withAuthToken(authToken)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createLoginNO(ErrorCode errorCode) {
    return new WordleHttpResponse.Builder().withType(Type.LOGIN_NO).withErrorCode(errorCode)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createTooLargeRequest() {
    return new WordleHttpResponse.Builder().withType(Type.TOOLARGE).withFullyPopulated(true)
        .build();
  }

  @Override
  public WordleResponse createBadRequest() {
    return new WordleHttpResponse.Builder().withType(Type.BAD).withFullyPopulated(true).build();
  }
}
