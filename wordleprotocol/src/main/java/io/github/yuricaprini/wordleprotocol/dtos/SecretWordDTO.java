package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Objects;

/**
 * A {@code SecretWordDTO} represents an immutable data transfer object used to hold information
 * about the secret word in a Wordle game. It includes the word number, the actual secret word, 
 * and its translated version.
 */
public class SecretWordDTO {

  private final int wordNumber;
  private final String secretWord;
  private final String translatedSecretWord;

  /**
   * Static factory method to create a new instance of {@code SecretWordDTO} with the specified
   * {@code wordNumber}, {@code secretWord}, and {@code translatedSecretWord}.
   *
   * @param wordNumber the number associated with the secret word.
   * @param secretWord the actual secret word.
   * @param translatedSecretWord the translated version of the secret word.
   * @return a new {@code SecretWordDTO} instance containing {@code wordNumber}, {@code secretWord},
   * {@code translatedSecretWord}.
   */
  public static SecretWordDTO newInstance(int wordNumber, String secretWord,
      String translatedSecretWord) {
    return new SecretWordDTO(wordNumber, Objects.requireNonNull(secretWord),
        Objects.requireNonNull(translatedSecretWord));
  }

  /**
  * Private constructor to create a {@code SecretWordDTO} object with the specified 
  * {@code wordNumber}, {@code secretWord}, and {@code translatedSecretWord}.
  *
  * @param wordNumber the number associated with the secret word.
  * @param secretWord the actual secret word.
  * @param translatedSecretWord the translated version of the secret word.
  */
  private SecretWordDTO(Integer wordNumber, String secretWord, String translatedSecretWord) {
    this.wordNumber = wordNumber;
    this.secretWord = secretWord;
    this.translatedSecretWord = translatedSecretWord;
  }

  /**
   * Returns the number associated with the secret word.
   *
   * @return the number associated with the secret word.
   */
  public Integer getWordNumber() {
    return wordNumber;
  }

  /**
  * Returns the actual secret word that the user has to guess in a Wordle game.
  *
  * @return the actual secret word or {@code null} if no secret word is set.
  */
  public String getSecretWord() {
    return secretWord;
  }

  /**
   * Returns the translated version of the secret word.
   *
   * @return the translated version of the secret word or {@code null} if no translated secret word 
   * is set.
   */
  public String getTranslatedSecretWord() {
    return translatedSecretWord;
  }
}
