package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.Objects;

/**
 * The {@code Score} class represents the score of a user in the Wordle game.
 * <p>
 * Scores are immutable.
 * 
 * @author Yuri Caprini
 */
public class Score implements Comparable<Score> {

  private int value;

  /**
   * Constructs a new {@code Score} with the specified integer value.
   *
   * @param value the integer value of the score.
   */
  public Score(int value) {
    this.value = value;
  }

  /**
   * Gets the integer value of the score.
   *
   * @return the integer value of the score.
   */
  public int getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Score score = (Score) obj;
    return value == score.value;
  }

  @Override
  public int compareTo(Score other) {
    return Integer.compare(this.value, other.value);
  }
}
