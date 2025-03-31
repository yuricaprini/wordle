package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import io.github.yuricaprini.wordleserver.circle02usecases.TimeUseCase;
import io.github.yuricaprini.wordleserver.circle03_adapters.SecretWordRefresher;

public class RefreshRequestAdapter implements SecretWordRefresher {

  private TimeUseCase refreshSecretWordUseCase;

  public RefreshRequestAdapter(TimeUseCase refreshSecretWordUseCase) {
    this.refreshSecretWordUseCase = refreshSecretWordUseCase;
  }

  @Override
  public void refresh() throws Exception {
    refreshSecretWordUseCase.execute();
  }
}
