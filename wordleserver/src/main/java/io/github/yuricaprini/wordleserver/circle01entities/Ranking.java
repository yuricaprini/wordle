package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.javatuples.Pair;

/**
 * The {@code Ranking} class represents the Wordle player ranking.
 * 
 * @author Yuri Caprini
 */
public class Ranking {

  private volatile static Ranking instance;
  private TreeMap<Score, ArrayList<Username>> rankingMap;

  private Ranking() {
    rankingMap = new TreeMap<Score, ArrayList<Username>>();
  }

  /**
   * Gets the singleton instance of the {@code Ranking} class.
   *
   * @return the singleton instance.
   */
  public static Ranking getInstance() {
    if (instance == null) {
      synchronized (Ranking.class) {
        if (instance == null)
          instance = new Ranking();
      }
    }
    return instance;
  }

  /**
   * Adds a new player to the ranking.
   *
   * @param username the name of the player.
   * @param score the player score.
   */
  public synchronized void add(Username username, Score score) {
    put(username, score);
  }

  /**
   * Updates the score for a given player in the ranking and returns the new top 3 rankings if 
   * they have changed.
   *
   * @param username the name of the player whose score has to be updated.
   * @param oldScore the old score associated with the player.
   * @param newScore the new score to update for the player.
   * @return the new top 3 rankings if they have changed, {@code null} otherwise.
   */
  public synchronized ArrayList<Pair<Username, Score>> update(Username username, Score oldScore,
      Score newScore) {

    ArrayList<Pair<Username, Score>> oldTop3 = getTop3();
    remove(username, oldScore);
    put(username, newScore);
    ArrayList<Pair<Username, Score>> currentTop3 = getTop3();
    if (!currentTop3.equals(oldTop3))
      return currentTop3;

    return null;
  }

  /**
   * Retrieves the first {@code limit} entries of the ranking from the bottom up.
   *
   * @param limit the maximum number of entries to retrieve.
   * @return a list of username-score pairs in the bottom-up ranking order.
   */
  public synchronized ArrayList<Pair<Username, Score>> getBottomUp(int limit) {
    ArrayList<Pair<Username, Score>> result = new ArrayList<>();
    int count = 0;

    for (Entry<Score, ArrayList<Username>> entry : rankingMap.entrySet()) {
      for (Username username : entry.getValue()) {
        result.add(new Pair<>(username, entry.getKey()));
        count++;
        if (count >= limit)
          return result;
      }
    }

    return result;
  }

  /**
   * Retrieves the bottom-up ranking starting from a specific username and score, up to the 
   * specified limit.
   *
   * @param startingUsername the username to start the ranking from.
   * @param startingScore the score associated with the starting username.
   * @param limit the maximum number of entries to retrieve.
   * @return a list of username-score pairs in the bottom-up ranking order.
   */
  public synchronized ArrayList<Pair<Username, Score>> getBottomUp(Username startingUsername,
      Score startingScore, int limit) {

    NavigableMap<Score, ArrayList<Username>> tailMap = rankingMap.tailMap(startingScore, true);
    ArrayList<Pair<Username, Score>> result = new ArrayList<>();
    int count = 0;
    boolean foundStartingUsername = false;
    boolean usernameNotInMap = false;
    for (Map.Entry<Score, ArrayList<Username>> entry : tailMap.entrySet()) {
      if (!usernameNotInMap) {
        for (Username username : entry.getValue()) {
          if (foundStartingUsername) {
            result.add(new Pair<>(username, entry.getKey()));
            count++;
            if (count >= limit)
              return result;

          } else if (username.equals(startingUsername))
            foundStartingUsername = true;
        }
      }

      if (!foundStartingUsername) {
        for (Username username : entry.getValue()) {
          result.add(new Pair<>(username, entry.getKey()));
          count++;
          if (count >= limit)
            return result;

        }
        usernameNotInMap = true;
      }
    }
    return result;
  }

  private void remove(Username username, Score score) {
    ArrayList<Username> usernames = rankingMap.get(score);
    if (usernames != null) {
      usernames.remove(username);
      if (usernames.isEmpty())
        rankingMap.remove(score);
    }
  }

  private void put(Username username, Score score) {
    rankingMap.compute(score, (key, value) -> {
      if (value == null)
        value = new ArrayList<Username>();

      value.add(username);
      return value;
    });
  }

  private ArrayList<Pair<Username, Score>> getTop3() {
    ArrayList<Pair<Username, Score>> top3 = new ArrayList<Pair<Username, Score>>();
    int count = 0;

    for (Map.Entry<Score, ArrayList<Username>> entry : rankingMap.descendingMap().entrySet()) {
      for (Username username : entry.getValue()) {
        top3.add(new Pair<Username, Score>(username, entry.getKey()));
        count++;
        if (count >= 3)
          return top3;
      }
    }

    return top3;
  }
}


