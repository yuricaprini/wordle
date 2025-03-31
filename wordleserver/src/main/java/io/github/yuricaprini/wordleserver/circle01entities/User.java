package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.Objects;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.GameAlreadyPlayedException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.GameNotStartedException;

/**
 * A {@code User} represents a Wordle user.
 * 
 * @author Yuri Caprini
 */
public class User {

  private Username username;
  private Password password;
  private Game lastGame;
  private Stats stats;

  /**
   * Creates a new istance of {@code User}.
   * 
   * @param username the username of this newly created user.
   * @param password the password of this newly created user.
   * @throws NullPointerException if {@code username == null} || {@code password == null}.
   */
  public User(Username username, Password password) {

    this.username = Objects.requireNonNull(username);
    this.password = Objects.requireNonNull(password);
    this.lastGame = new Game(SecretWord.getCopy());
    this.stats = new Stats();
  }

  /**
   * Gets this user username.
   * 
   * @return the username of this user.
   */
  public Username getUsername() {
    return username;
  }

  /**
   * Gets this user password.
   * 
   * @return the password of this user.
   */
  public Password getPassword() {
    return password;
  }

  /**
  * Starts a new game for the user if and only if the secret word has been refreshed.
  * If the secret word has not been refreshed and a game is in progress the clues of this
  * game are returned, without starting a new game. If the secret word has been refreshed and a 
  * game is in progress, this game is lost a new game starts.
  *
  * @return the clues of the game in progress or an empty array if game has just started.
  * @throws GameAlreadyPlayedException if the user attempts to start a new game and the secret word
  * has not been refreshed and the user has already played the game for the current secret word.
  */
  public synchronized Clue[] requestsNewGame() throws GameAlreadyPlayedException {

    SecretWord currentWord = SecretWord.getCopy();
    if (lastGame.getSecretWord().equals(currentWord))

      if (!lastGame.isOver()) { // game is in progress or not started for the current word
        lastGame.markAsStarted();
        return lastGame.getCluesCopy();
      } else
        throw new GameAlreadyPlayedException();

    else {// secret word refreshed

      if (!lastGame.isOver()) // game is in progress for the old word is lost
        stats.addGameLost();

      lastGame = new Game(currentWord);
      lastGame.markAsStarted();
      return this.lastGame.getCluesCopy();
    }
  }

  /**
  * Attempts to guess the secret word in the user current game. If the secret word has not been 
  * guessed a clue is added to the game state. If the secret word has been guessed (game won) or 
  * there are no more attempts (game lost) the stats are updated properly.
  *
  * @param word the user guess.
  * @return A triplet containing clues, the secret word (if game is over), and user statistics(if * game is over).
  * @throws GameAlreadyPlayedException if the user has already played a game for the current secret
  * word.
  * @throws GameNotStartedException if the user attempts to guess before starting a game.
  */
  public synchronized Triplet<Clue[], SecretWord, Stats> attemptsToGuess(Word word)
      throws GameAlreadyPlayedException, GameNotStartedException {

    if (!lastGame.isStarted())
      throw new GameNotStartedException();

    if (lastGame.isOver())
      throw new GameAlreadyPlayedException();

    lastGame.addClue(new Clue(word, lastGame.getSecretWord()));

    if (lastGame.isWon())
      stats.addGameWon(lastGame.getAttempts());

    if (lastGame.isLost())
      stats.addGameLost();

    if (lastGame.isOver())
      return new Triplet<Clue[], SecretWord, Stats>(lastGame.getCluesCopy(),
          lastGame.getSecretWordCopy(), stats.getCopy());

    return new Triplet<Clue[], SecretWord, Stats>(lastGame.getCluesCopy(), null, null);
  }

  /**
  * Gets the user's game statistics.
  *
  * @return A copy of the user's game statistics.
  */
  public synchronized Stats getStats() {
    return stats.getCopy();
  }

  /**
  * Gets the result of the last completed game, if any.
  *
  * @return a pair containing the secret word and clues of the last completed game, 
  * or {@code null} if the last game is in progress.
  */
  public synchronized Pair<SecretWord, Clue[]> getGameResult() {
    if (!lastGame.isOver())
      return null;

    return new Pair<SecretWord, Clue[]>(lastGame.getSecretWord(), lastGame.getCluesCopy());
  }

  @Override
  public boolean equals(Object o) {

    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    User anotherUser = (User) o;

    return this.username.equals(anotherUser.username) && this.password.equals(anotherUser.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }

  @Override
  public String toString() {
    return "User{" + "username=" + username + ", password=" + password + '}';
  }
}
