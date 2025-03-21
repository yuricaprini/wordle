package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import io.github.yuricaprini.wordleserver.circle02usecases.AdminUseCase;
import io.github.yuricaprini.wordleserver.circle03_adapters.ServerStatus;

public class PersistenceRequestAdapter implements ServerStatus {

  private AdminUseCase loadEntitiesUseCase;
  private AdminUseCase storeEntitiesUseCase;

  public PersistenceRequestAdapter(AdminUseCase loadEntitiesUseCase,
      AdminUseCase storeEntitiesUseCase) {

    this.loadEntitiesUseCase = loadEntitiesUseCase;
    this.storeEntitiesUseCase = storeEntitiesUseCase;
  }

  @Override
  public boolean load() throws Exception {
    return loadEntitiesUseCase.execute();
  }

  @Override
  public boolean store() throws Exception {
    return storeEntitiesUseCase.execute();
  }

}
