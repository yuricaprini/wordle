package io.github.yuricaprini.wordleserver.circle02usecases;

import java.io.IOException;
import java.util.Iterator;
import io.github.yuricaprini.wordleserver.circle01entities.User;

public interface EntityRepository {

  void pushAll(Iterator<User> iterator) throws IOException;

  Iterator<User> loadAll() throws Exception;

}
