package io.github.yuricaprini.wordleserver.circle05configurations.factories;

import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleserver.circle02usecases.RegisteredUserUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.Login;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.PlayWordle;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.ShowMeStats;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.SendWord;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.Share;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.ShowMeRanking;
import io.github.yuricaprini.wordleserver.circle05configurations.AppConfig;

public class RegisteredUserUseCaseFactory implements RegisteredUserUseCase.Factory {

  @Override
  public RegisteredUserUseCase createUseCase(WordleRequest.Type requestType) {

    switch (requestType) {

      case LOGIN:
        return new Login(ProtocolFactoryProvider.newWordleResponseFactory());

      case PLAY_WORDLE:
        return new PlayWordle(ProtocolFactoryProvider.newWordleResponseFactory());

      case SEND_WORD:
        return new SendWord(ProtocolFactoryProvider.newWordleResponseFactory(),
            AppConfig.getNewSecretWordRepository(), AppConfig.getNewTop3Notifier());

      case SHOWME_STATS:
        return new ShowMeStats(ProtocolFactoryProvider.newWordleResponseFactory());

      case SHOWME_RANKING:
        return new ShowMeRanking(ProtocolFactoryProvider.newWordleResponseFactory());

      case SHARE:
        return new Share(AppConfig.getNewGameResultSharer(),
            ProtocolFactoryProvider.newWordleResponseFactory());
    }
    return null; // never reached
  }

}
