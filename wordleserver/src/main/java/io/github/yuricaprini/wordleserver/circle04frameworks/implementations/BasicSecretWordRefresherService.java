package io.github.yuricaprini.wordleserver.circle04frameworks.implementations;

import io.github.yuricaprini.wordleserver.circle03_adapters.SecretWordRefresher;
import io.github.yuricaprini.wordleserver.circle04frameworks.SecretWordRefresherService;

public class BasicSecretWordRefresherService extends SecretWordRefresherService {

  private SecretWordRefresher secretWordRefresher;

  public BasicSecretWordRefresherService(Long secretWordRefreshInterval,
      SecretWordRefresher secretWordRefresher) {

    super(secretWordRefreshInterval);
    this.secretWordRefresher = secretWordRefresher;
  }

  @Override
  protected void refreshSecretWord() throws Exception {
    secretWordRefresher.refresh();
  }
}
