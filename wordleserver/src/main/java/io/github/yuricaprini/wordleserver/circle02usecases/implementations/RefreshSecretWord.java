package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import io.github.yuricaprini.wordleserver.circle01entities.SecretWord;
import io.github.yuricaprini.wordleserver.circle02usecases.SecretWordRepository;
import io.github.yuricaprini.wordleserver.circle02usecases.TimeUseCase;
import io.github.yuricaprini.wordleserver.circle02usecases.WordTranslator;

public class RefreshSecretWord implements TimeUseCase {

  private SecretWordRepository secretWordRepo;
  private WordTranslator wordTranslator;

  public RefreshSecretWord(SecretWordRepository secretWordRepo, WordTranslator wordTranslator) {
    this.secretWordRepo = secretWordRepo;
    this.wordTranslator = wordTranslator;
  }

  @Override
  public boolean execute() throws Exception {

    String secretWord = secretWordRepo.getRandom();
    String translation = wordTranslator.translate(secretWord);

    SecretWord.setNew(secretWord, translation);
    System.out.println(">>> secret word: " + secretWord + " <<<"); //useful to manual test

    return true;
  }
}
