package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Arrays;

/**
 * A {@code GameStateDTO} represents an immutable data transfer object encapsulating the state of a 
 * Wordle game.
 * It includes an array of clues provided by the game to the user, information about the secret 
 * word, and statistics related to the user performance in the game.
 */
public class GameStateDTO {

  private final ClueDTO[] clues;
  private final SecretWordDTO secretWord;
  private final StatsDTO stats;

  /**
   * Static factory method to create a new instance of {@code GameStateDTO} with the specified
   * {@code clues}, {@code word}, and {@code stats}.
   *
   * @param clues an array of {@code ClueDTO} representing the clues provided to the user.
   * @param secretWord a {@code SecretWordDTO} representing information about the secret word.
   * @param stats a {@code StatsDTO} representing statistics related to the user performance.
   * @return a new {@code GameStateDTO} instance containing the specified clues, secret word, and 
   * statistics.
   */
  public static GameStateDTO newInstance(ClueDTO[] clues, SecretWordDTO secretWord,
      StatsDTO stats) {
    return new GameStateDTO(clues, secretWord, stats);
  }

  /**
   * Private constructor to create a {@code GameStateDTO} object with the specified {@code clues}, 
   * {@code word}, and {@code stats}.
   *
   * @param clues an array of {@code ClueDTO} representing the clues provided to the user.
   * @param secretWord a {@code SecretWordDTO} representing information about the secret word.
   * @param stats a {@code StatsDTO} representing statistics related to the user performance.
   */
  private GameStateDTO(ClueDTO[] clues, SecretWordDTO secretWord, StatsDTO stats) {
    this.clues = clues;
    this.secretWord = secretWord;
    this.stats = stats;
  }

  /**
   * Returns an array of {@code ClueDTO} representing the clues provided to the user by the game.
   *
   * @return an array of {@code ClueDTO} representing the clues provided to the user by the game
   * or {@code null} if no {@code ClueDTO} is set.
   */
  public ClueDTO[] getCluesDTO() {
    return clues == null ? null : Arrays.copyOf(clues, clues.length);
  }

  /**
   * Returns a {@code SecretWordDTO} representing information about the secret word.
   *
   * @return a {@code SecretWordDTO} representing information about the secret word or {@code null} 
   * if no {@code SecretWordDTO} is set.
   */
  public SecretWordDTO getSecretWordDTO() {
    return secretWord;
  }

  /**
   * Returns a {@code StatsDTO} representing statistics related to the user performance.
   *
   * @return a {@code SecretWordDTO} representing statistics related to the user performance or
   * {@code null} if no {@code StatsDTO} is set.
   */
  public StatsDTO getStatsDTO() {
    return stats;
  }
}
