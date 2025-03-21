package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import java.util.Iterator;
import io.github.yuricaprini.wordleserver.circle01entities.Ranking;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle02usecases.AdminUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.EntityRepository;

public class LoadEntities implements AdminUseCase {

  private EntityRepository entityRepository;

  public LoadEntities(EntityRepository entityRepository) {
    this.entityRepository = entityRepository;
  }

  @Override
  public boolean execute() throws Exception {
    Iterator<User> loadedUsers = this.entityRepository.loadAll();

    while (loadedUsers.hasNext()) {

      User loadedUser = loadedUsers.next();
      RegisteredUsers.getInstance().add(loadedUser);
      Ranking.getInstance().add(loadedUser.getUsername(), loadedUser.getStats().getScore());
    }
    return true;
  }
}
