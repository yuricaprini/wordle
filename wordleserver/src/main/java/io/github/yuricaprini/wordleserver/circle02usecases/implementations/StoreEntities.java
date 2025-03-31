package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import java.util.Iterator;
import io.github.yuricaprini.wordleserver.circle01entities.RegisteredUsers;
import io.github.yuricaprini.wordleserver.circle01entities.User;
import io.github.yuricaprini.wordleserver.circle02usecases.AdminUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.EntityRepository;

public class StoreEntities implements AdminUseCase {

  private EntityRepository entityRepository;

  public StoreEntities(EntityRepository entityRepository) {
    this.entityRepository = entityRepository;
  }

  @Override
  public boolean execute() throws Exception {
    Iterator<User> iterator = RegisteredUsers.getInstance().getUserIterator();
    entityRepository.pushAll(iterator);
    return true;
  }
}
