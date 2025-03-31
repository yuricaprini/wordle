package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Arrays;
import java.util.Objects;

/**
 * A {@code SharedGameResultDTO} represents an immutable data transfer object used to share Wordle
 * player game results.
 * It includes the player's name, the Wordle number, and an array of strings representing the color 
 * pattern associated with the correctness of each letter in the guessed word relative to the
 * Wordle Secret Word (SW).
 *
 * - If wordColors[i].charAt(j) == '+' then the j-th letter of the i-th word is in the correct 
 * position w.r.t. SW.
 * - If wordColors[i].charAt(j) == '?' then the j-th letter of the i-th word is present but in the 
 * wrong position w.r.t. SW.
 * - If wordColors[i].charAt(j) == 'X' then the j-th letter of the i-th word is not present in SW.
 */
public class SharedGameResultDTO {

  private final String playername;
  private final Integer wordleNumber;
  private final String[] wordColors;

  /**
   * Static factory method to create a new instance of {@code SharedGameResultDTO} with the 
   * specified {@code playername}, {@code wordleNumber}, and {@code wordColors}.
   *
   * @param playername the name of the player.
   * @param wordleNumber the Wordle number associated with the game.
   * @param wordColors an array of strings representing the color patterns associated with 
   * correctness.
   * @return a new {@code SharedGameResultDTO} instance containing {@code playername}, 
   * {@code wordleNumber}, and {@code wordColors}.
   * @throws NullPointerException if {@code playername == null || wordColors == null}.
   */
  public static SharedGameResultDTO newInstance(String playername, Integer wordleNumber,
      String[] wordColors) {
    return new SharedGameResultDTO(Objects.requireNonNull(playername),
        Objects.requireNonNull(wordleNumber), Objects.requireNonNull(wordColors));
  }

  /**
   * Private constructor to create a {@code SharedGameResultDTO} object with the specified 
   * {@code playername}, {@code wordleNumber}, and {@code wordColors}.
   *
   * @param playername    the name of the player.
   * @param wordleNumber  the Wordle number associated with the game.
   * @param wordColors    an array of strings representing the color patterns associated with 
   * correctness.
   */
  private SharedGameResultDTO(String playername, Integer wordleNumber, String[] wordColors) {
    this.playername = playername;
    this.wordleNumber = wordleNumber;
    this.wordColors = Objects.requireNonNull(wordColors).clone(); // Cloning to ensure immutability.
  }

  /**
   * Returns the name of the player.
   *
   * @return the name of the player.
   */
  public String getPlayername() {
    return playername;
  }

  /**
   * Returns the Wordle number associated with the game.
   *
   * @return the Wordle number associated with the game.
   */
  public Integer getWordleNumber() {
    return wordleNumber;
  }

  /**
   * Returns an array of strings representing the color patterns associated with correctness.
   *
   * @return an array of strings representing the color patterns associated with correctness.
   */
  public String[] getWordsColors() {
    return Arrays.copyOf(wordColors, wordColors.length); //ensure immutability.
  }
}
