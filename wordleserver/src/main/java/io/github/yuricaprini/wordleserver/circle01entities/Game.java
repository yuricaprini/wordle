package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@code Game} class represents a Wordle game session. It manages the state of the game,
 * including the secret word, clues provided by the player, and the game status.
 * 
 * @author Yuri Caprini
 */
public class Game {

  /**
  * The maximum number of attempts allowed in a game.
  */
  public static final int MAX_ATTEMPTS = 12;

  private SecretWord secretWord;
  private ArrayList<Clue> clues;
  private boolean isStarted;

  /**
   * Creates a new instance of {@code Game} with the specified secret word.
   *
   * @param secretWord the secret word for the game.
   * @throws NullPointerException if {@code secretWord == null}
   */
  public Game(SecretWord secretWord) {
    this.secretWord = Objects.requireNonNull(secretWord);
    this.clues = new ArrayList<Clue>(MAX_ATTEMPTS);
    this.isStarted = false;
  }

  /**
   * Adds a clue to the game state if the game is still in progress.
   *
   * @param clue the clue to add to the game.
   * @return {@code true} if the clue was successfully added, {@code false} if the game is already 
   * over.
   * @throws NullPointerException if {@code clue == null}
   */
  public boolean addClue(Clue clue) {
    return !isOver() && clues.add(Objects.requireNonNull(clue));
  }

  /**
   * Checks if the game is over, either due to a win or loss.
   *
   * @return {@code true} if the game is over, {@code false} otherwise.
   */
  public boolean isOver() {
    return isWon() || isLost();
  }

  /**
   * Checks if the game is won.
   *
   * @return {@code true} if the game is won, {@code false} otherwise.
   */
  public boolean isWon() {
    if (clues.size() == 0)
      return false;
    return clues.get(clues.size() - 1).matchAllGreen();
  }

  /**
   * Checks if the game is lost due to reaching the maximum number of attempts.
   *
   * @return {@code true} if the game is lost, {@code false} otherwise.
   */
  public boolean isLost() {
    return clues.size() == MAX_ATTEMPTS;
  }

  /**
   * Gets the secret word associated with the game.
   *
   * @return the secret word.
   */
  public SecretWord getSecretWord() {
    return secretWord;
  }

  /**
  * Gets the list of clues provided during the game.
  *
  * @return a list of clues.
  */
  public List<Clue> getClues() {
    return clues;
  }

  /**
   * Gets the number of attempts made in the game.
   *
   * @return the number of attempts.
   */
  public int getAttempts() {
    return clues.size();
  }

  /**
   * Gets a copy of the secret word associated with the game.
   *
   * @return a copy of the secret word.
   */
  public SecretWord getSecretWordCopy() {
    return secretWord;
  }

  /**
   * Gets an array containing copies of all clues provided during the game.
   *
   * @return an array of clues.
   */
  public Clue[] getCluesCopy() {
    Clue[] cluesArray = new Clue[clues.size()];
    return clues.toArray(cluesArray);
  }

  /**
  * Marks the game as started.
  */
  public void markAsStarted() {
    isStarted = true;
  }

  /**
   * Checks if the game has been started.
   *
   * @return {@code true} if the game has been started, {@code false} otherwise.
   */
  public boolean isStarted() {
    return isStarted;
  }
}
