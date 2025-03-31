package io.github.yuricaprini.wordleprotocol.implementations.factories;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.dtos.GameStateDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;
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

  @Override
  public WordleResponse createPlayWordleOK(ClueDTO[] clues) {
    return new WordleHttpResponse.Builder().withType(Type.PLAYWORDLE_OK).withClues(clues)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createPlayWordleNO(ErrorCode errorCode) {
    return new WordleHttpResponse.Builder().withType(Type.PLAYWORDLE_NO).withErrorCode(errorCode)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createSendWordOK(GameStateDTO gameStateDTO) {
    return new WordleHttpResponse.Builder().withType(Type.SENDWORD_OK).withGameState(gameStateDTO)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createSendWordNO(ErrorCode errorCode) {
    return new WordleHttpResponse.Builder().withType(Type.SENDWORD_NO).withErrorCode(errorCode)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createShowMeStatsOK(StatsDTO statsToDTO) {
    return new WordleHttpResponse.Builder().withType(Type.SHOWMESTATS_OK).withStats(statsToDTO)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createShowMeStatsNO(ErrorCode errorCode) {
    return new WordleHttpResponse.Builder().withType(Type.SHOWMESTATS_NO).withErrorCode(errorCode)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createShowMeRankingOK(PlayerDTO[] playerDTOs) {
    return new WordleHttpResponse.Builder().withType(Type.SHOWMERANKING_OK).withPlayers(playerDTOs)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createShowMeRankingNO(ErrorCode errorCode) {
    return new WordleHttpResponse.Builder().withType(Type.SHOWMERANKING_NO).withErrorCode(errorCode)
        .withFullyPopulated(true).build();
  }

  @Override
  public WordleResponse createShareOK() {
    return new WordleHttpResponse.Builder().withType(Type.SHARE_OK).withFullyPopulated(true)
        .build();
  }

  @Override
  public WordleResponse createShareNO(ErrorCode errorCode) {
    return new WordleHttpResponse.Builder().withType(Type.SHARE_NO).withErrorCode(errorCode)
        .withFullyPopulated(true).build();
  }
}
