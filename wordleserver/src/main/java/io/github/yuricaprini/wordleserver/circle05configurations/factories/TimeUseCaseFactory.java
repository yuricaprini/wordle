package io.github.yuricaprini.wordleserver.circle05configurations.factories;

import io.github.yuricaprini.wordleserver.circle02usecases.SecretWordRepository;
import io.github.yuricaprini.wordleserver.circle02usecases.TimeUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.implementations.RefreshSecretWord;
import io.github.yuricaprini.wordleserver.circle03_adapters.implementations.TranslationRequestAdapter;

public class TimeUseCaseFactory implements TimeUseCase.Factory {

  @Override
  public TimeUseCase createRefreshSecretWord(SecretWordRepository secretWordRepository) {
    return new RefreshSecretWord(secretWordRepository, new TranslationRequestAdapter());
  }
}
