package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Stats} class represents the statistics of a user performance in Wordle games.
 * <p>
 * This class tracks the total number of games played, games won, current and maximum win streaks, 
 * guess distribution, and calculates the user score based on their performance.
 * 
 * @author Yuri Caprini
 */
public class Stats {

  private static final int WIN_REWARD = 10;
  private int totalGames;
  private int wonGames;
  private int currentStreak;
  private int maxStreak;
  private HashMap<Integer, Integer> guessDistribution;
  private Score oldscore;

  /**
   * Constructs a new {@code Stats} object with initial values.
   */
  public Stats() {
    totalGames = 0;
    wonGames = 0;
    currentStreak = 0;
    maxStreak = 0;
    guessDistribution = new HashMap<Integer, Integer>();

    for (int i = 1; i <= Game.MAX_ATTEMPTS; i++) {
      guessDistribution.put(i, 0);
    }

    oldscore = getScore();
  }

  private Stats(int totalGames, int wonGames, int currentStreak, int maxStreak,
      HashMap<Integer, Integer> guessDistribution, Score oldScore) {
    this.totalGames = totalGames;
    this.wonGames = wonGames;
    this.currentStreak = currentStreak;
    this.maxStreak = maxStreak;
    this.guessDistribution = guessDistribution;
    this.oldscore = oldScore;
  }

  /**
   * Updates the statistics when a game is won.
   *
   * @param guesses the number of guesses made in the winning game.
   */
  public void addGameWon(int guesses) {
    totalGames++;
    wonGames++;
    currentStreak++;
    maxStreak = Math.max(maxStreak, currentStreak);
    oldscore = getScore();
    guessDistribution.put(guesses, guessDistribution.getOrDefault(guesses, 0) + 1);
  }

  /**
   * Updates the statistics when a game is lost.
   */
  public void addGameLost() {
    totalGames++;
    currentStreak = 0;
  }

  /**
   * Gets the total number of games played.
   *
   * @return the total number of games played.
   */
  public int getPlayed() {
    return totalGames;
  }

  /**
   * Calculates the win percentage based on the total number of games played.
   *
   * @return the win percentage.
   */
  public int getWinPercentage() {
    if (totalGames == 0)
      return 0;

    return Math.round(((float) wonGames / totalGames) * 100);
  }

  /**
   * Gets the current win streak.
   *
   * @return the current win streak.
   */
  public int getCurrentStreak() {
    return currentStreak;
  }

  /**
   * Gets the maximum win streak achieved.
   *
   * @return the maximum win streak.
   */
  public int getMaxStreak() {
    return maxStreak;
  }

  /**
   * Gets the distribution of guesses made in games.
   *
   * @return a map representing the distribution of guesses.
   */
  public Map<Integer, Integer> getGuessDistribution() {
    return guessDistribution;
  }

  /**
   * Gets the score calculated before the last game won has added to the stats.
   *
   * @return the old user score.
   */
  public Score getOldScore() {
    return oldscore;
  }

  /**
   * Gets the score based on the actual stats.
   *
   * @return the actual user score.
   */
  public Score getScore() {
    int sum = 0;

    for (Map.Entry<Integer, Integer> entry : guessDistribution.entrySet()) {
      sum += ((Game.MAX_ATTEMPTS - entry.getKey()) + WIN_REWARD) * entry.getValue();
    }

    return new Score(sum);
  }

  /**
   * Creates a copy of the {@code Stats} object.
   *
   * @return a copy of the {@code Stats} object.
   */
  public Stats getCopy() {
    return new Stats(totalGames, wonGames, currentStreak, maxStreak,
        new HashMap<Integer, Integer>(guessDistribution), oldscore);
  }
}
