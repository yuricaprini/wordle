package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Objects;

/**
 * A {@code ClueDTO} represents an immutable data transfer object used to encapsulate Wordle hint,
 * aiding the user in guessing the secret word. It includes the word provided by the user as an 
 * attempt to guess the secret word and a string representing the color pattern associated with 
 * the correctness of each letter in the word relative to the Wordle Secret Word (SW):
 * 
 * - if wordColors.charAt(i) == '+' then word.charAt(i) is the correct position w.r.t. SW
 * - if wordColors.charAt(i) == '?' then word.charAt(i) is present but in wrong position w.r.t. SW
 * - if wordColors.charAt(i) == 'X' then word.charAt(i) is not present in SW
*/
public class ClueDTO {

  private final String word;
  private final String wordColors;

  /**
   * Static factory method to create a new instance of {@code ClueDTO} with the specified
   * {@code word} and {@code wordColors}.
   *
   * @param word the word provided by the user in an attempt to guess the secret word.
   * @param wordColors a string representing the color pattern associated with the word correctness.
   * @return a new {@code ClueDTO} instance containing {@code word} and {@code wordColors}.
   * @throws NullPointerException if {@code word == null || wordColors == null}.
   */
  public static ClueDTO newInstance(String word, String wordColors) {
    return new ClueDTO(Objects.requireNonNull(word), Objects.requireNonNull(wordColors));
  }

  /**
   * Private constructor to create a {@code ClueDTO} object with the specified {@code word} and 
   * {@code wordColors}.
   *
   * @param word the word provided by the user in an attempt to guess the secret word.
   * @param wordColors a string representing the color pattern associated with the word correctness.
   */
  private ClueDTO(String word, String wordColors) {
    this.word = word;
    this.wordColors = wordColors;
  }

  /**
   * Returns the word provided by the user as an attempt to guess the secret word.
   *
   * @return the word provided by the user as an attempt to guess the secret word.
   */
  public String getWord() {
    return word;
  }

  /**
   * Returns the string representing the color pattern associated with the correctness of each
   * letter in the word relative to the Wordle secret word (SW).
   *
   * @return the string representing the color pattern associated with the correctness of each
   * letter in the word relative to the Wordle secret word (SW).
   */
  public String getWordColors() {
    return wordColors;
  }
}
