package io.github.yuricaprini.wordleserver.circle02usecases;

import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;

public interface Top3Notifier {

  public void notify(PlayerDTO[] top3);

}
