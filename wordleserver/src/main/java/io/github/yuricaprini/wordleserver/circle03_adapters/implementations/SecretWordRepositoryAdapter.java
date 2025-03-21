package io.github.yuricaprini.wordleserver.circle03_adapters.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import io.github.yuricaprini.wordleserver.circle02usecases.SecretWordRepository;

public class SecretWordRepositoryAdapter implements SecretWordRepository {

  private static final String DEFAULT_VOCABULARY_FILENAME = "words.txt";
  private static volatile SecretWordRepository instance;
  private static volatile boolean loaded;

  private HashSet<String> vocabulary;


  public static SecretWordRepository getInstance() {
    if (instance == null) {
      synchronized (SecretWordRepository.class) {
        if (instance == null)
          instance = new SecretWordRepositoryAdapter();
      }
    }
    return instance;
  }

  private SecretWordRepositoryAdapter() {
    this.vocabulary = new HashSet<String>();
    loaded = false;
  }

  @Override
  public boolean contains(String word) throws IOException {
    open();
    return this.vocabulary.contains(Objects.requireNonNull(word));
  }

  @Override
  public String getRandom() throws IOException {
    open();
    int randomIndex = (int) (Math.floor(Math.random() * vocabulary.size()));
    int currentIndex = 0;
    String randomWord = null;

    for (String word : vocabulary) {
      randomWord = word;
      if (currentIndex == randomIndex)
        break;
      currentIndex++;
    }

    return randomWord;
  }

  private boolean open() throws IOException {
    if (!loaded) { //double check locking: ensures lock and load  only one time.
      synchronized (SecretWordRepositoryAdapter.class) {
        if (!loaded) {
          this.vocabulary = loadVocabulary();
          loaded = true;
        }
      }
    }
    return true;
  }

  private HashSet<String> loadVocabulary() throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread()
        .getContextClassLoader().getResourceAsStream(DEFAULT_VOCABULARY_FILENAME)));) {

      String line;
      HashSet<String> hashSet = new HashSet<String>();
      while ((line = reader.readLine()) != null) {
        hashSet.add(line.trim());
      }
      return hashSet;
    }
  }
}
