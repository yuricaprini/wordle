package io.github.yuricaprini.wordleserver.circle02usecases;

public interface TimeUseCase {

  public boolean execute() throws Exception;

  public interface Factory {
    public TimeUseCase createRefreshSecretWord(SecretWordRepository secretWordRepository);
  }
}
