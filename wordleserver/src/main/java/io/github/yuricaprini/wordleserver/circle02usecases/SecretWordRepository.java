package io.github.yuricaprini.wordleserver.circle02usecases;

import java.io.IOException;

public interface SecretWordRepository {

  boolean contains(String word) throws IOException;

  String getRandom() throws IOException;

}
