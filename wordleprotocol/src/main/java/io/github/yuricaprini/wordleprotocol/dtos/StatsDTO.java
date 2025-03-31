package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A {@code StatsDTO} represents a data transfer object encapsulating statistics related to the
 * performance of a user in a Wordle game.
 * It includes information such as the number of games played, win percentage, current streak,
 * maximum streak, and a distribution of attempts made to arrive at the solution of the game, in 
 * every game won by the user.
 */
public class StatsDTO {

  private final int played;
  private final int winPercentage;
  private final int currentStreak;
  private final int maxStreak;
  private final Map<Integer, Integer> guessDistribution;

  /**
   * Static factory method to create a new instance of {@code StatsDTO} with the specified
   * {@code played}, {@code winPercentage}, {@code currentStreak}, {@code maxStreak}, and {@code 
   * guessDistribution}.
   *
   * @param played the number of games played by the user.
   * @param winPercentage the win percentage of the user.
   * @param currentStreak the current winning streak of the user.
   * @param maxStreak the maximum winning streak achieved by the user.
   * @param guessDistribution a map representing distribution of attempts made to arrive at the 
   * solution of the game, in every game won by the user.
   * @return a new {@code StatsDTO} instance containing the specified statistics.
   * @throws NullPointerException if {@code guessDistribution == null}.
   */
  public static StatsDTO newInstance(int played, int winPercentage, int currentStreak,
      int maxStreak, Map<Integer, Integer> guessDistribution) {
    return new StatsDTO(played, winPercentage, currentStreak, maxStreak,
        Objects.requireNonNull(guessDistribution));
  }

  /**
   * Private constructor to create a {@code StatsDTO} object with the specified {@code played}, 
   * {@code winPercentage}, {@code currentStreak}, {@code maxStreak}, and {@code guessDistribution}.
   *
   * @param played the number of games played by the user.
   * @param winPercentage the win percentage of the user.
   * @param currentStreak the current winning streak of the user.
   * @param maxStreak the maximum winning streak achieved by the user.
   * @param guessDistribution a map representing the distribution of attempts made to arrive at the 
   * solution of the game, in every game won by the user.
   */
  private StatsDTO(int played, int winPercentage, int currentStreak, int maxStreak,
      Map<Integer, Integer> guessDistribution) {

    this.played = played;
    this.winPercentage = winPercentage;
    this.currentStreak = currentStreak;
    this.maxStreak = maxStreak;
    this.guessDistribution = guessDistribution;
  }

  /**
   * Returns the number of games played by the user.
   *
   * @return the number of games played by the user.
   */
  public int getPlayed() {
    return played;
  }

  /**
   * Returns the win percentage of the user.
   *
   * @return the win percentage of the user.
   */
  public int getWinPercentage() {
    return winPercentage;
  }

  /**
   * Returns the current winning streak of the user.
   *
   * @return the current winning streak of the user.
   */
  public int getCurrentStreak() {
    return currentStreak;
  }

  /**
   * Returns the maximum winning streak achieved by the user.
   *
   * @return the maximum winning streak achieved by the user.
   */
  public int getMaxStreak() {
    return maxStreak;
  }

  /**
   * Returns a map representing the distribution of attempts made to arrive at the solution of the 
   * game, in every game won by the user.
   * 
   * @return a map representing the distribution of attempts made to arrive at the solution of the 
   * game, in every game won by the user.
   */
  public Map<Integer, Integer> getGuessDistribution() {
    return new HashMap<Integer, Integer>(guessDistribution); //ensure immutability.
  }
}
