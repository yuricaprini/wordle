package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.Objects;

/**
 * The {@code Clue} class represents a clue generated during a Wordle game. It provides information
 * about the correctness of attempted word compared to the secret word.
 * 
 * @author Yuri Caprini
 */
public class Clue {

  /**
   * Symbol representing a correct letter in the correct position.
   */
  public static char GREEN = '+';

  /**
   * Symbol representing a correct letter in the incorrect position.
   */
  public static char YELLOW = '?';

  /**
   * Symbol representing an incorrect letter.
   */
  public static char GREY = 'X';

  private Word word;
  private String wordColors;
  private boolean allGreen;

  /**
   * Creates a new instance of {@code Clue} based on the attempted word and a secret word.
   *
   * @param attemptedWord the user guess.
   * @param secretWord the secret word to compare the attempted word against.
   * @throws NullPointerException if {@code attemptedWord == null} || {@code secretWord == null}.
   */
  public Clue(Word attemptedWord, Word secretWord) {

    String strAttemptedWord = Objects.requireNonNull(attemptedWord).getValue();
    String strSecretWord = Objects.requireNonNull(secretWord).getValue();
    StringBuilder builder = new StringBuilder();

    int greens = 0;
    for (int i = 0; i < SecretWord.FIXED_LEN; i++) {
      char attemptedChar = strAttemptedWord.charAt(i);
      char secretChar = strSecretWord.charAt(i);

      if (attemptedChar == secretChar) {
        builder.append(GREEN);
        greens++;
      } else {
        builder.append(strSecretWord.contains(String.valueOf(attemptedChar)) ? YELLOW : GREY);
      }
    }

    allGreen = (greens == SecretWord.FIXED_LEN);

    word = attemptedWord;
    wordColors = builder.toString();
  }

  /**
   * Checks if all letters in the attempted word are correct and in the correct positions.
   *
   * @return {@code true} if all letters are correct and in the correct positions, {@code false} 
   * otherwise.
   */
  public boolean matchAllGreen() {
    return allGreen;
  }

  /**
   * Gets the attempted word associated with this clue.
   *
   * @return the attempted word.
   */
  public Word getWord() {
    return word;
  }

  /**
  * Gets a string representation of the colors associated with each letter in the attempted word.
  *
  * @return A string of color symbols representing correctness of letters.
  */
  public String getWordColors() {
    return wordColors;
  }
}
