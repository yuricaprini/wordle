package io.github.yuricaprini.wordleserver.circle04frameworks.implementations;

import io.github.yuricaprini.wordleserver.circle03_adapters.ServerStatus;
import io.github.yuricaprini.wordleserver.circle04frameworks.PersistenceService;

public class BasicPersistenceService extends PersistenceService {

  private ServerStatus serverStatus;

  public BasicPersistenceService(ServerStatus serverStatus) {
    this.serverStatus = serverStatus;
  }

  @Override
  protected void loadStatus() throws Exception {
    serverStatus.load();
  }

  @Override
  protected void storeStatus() throws Exception {
    serverStatus.store();
  }
}
