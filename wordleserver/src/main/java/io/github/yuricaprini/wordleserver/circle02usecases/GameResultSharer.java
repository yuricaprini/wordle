package io.github.yuricaprini.wordleserver.circle02usecases;

import io.github.yuricaprini.wordleprotocol.dtos.SharedGameResultDTO;

public interface GameResultSharer {

  public void share(SharedGameResultDTO sharedGameResultDTO) throws Exception;
}
