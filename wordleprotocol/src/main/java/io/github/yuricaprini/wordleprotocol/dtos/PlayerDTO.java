package io.github.yuricaprini.wordleprotocol.dtos;

import java.io.Serializable;
import java.util.Objects;

/**
 * A {@code PlayerDTO} represents an immutable data transfer object containing information about a
 * player. It includes the player name and score.
 */
public class PlayerDTO implements Serializable {

  private final String name;
  private final int score;

  /**
   * Private constructor to create a {@code PlayerDTO} object with the specified {@code name} and 
   * {@code score}.
   *
   * @param name the name of the player.
   * @param score the score of the player.
   */
  private PlayerDTO(String name, int score) {
    this.name = Objects.requireNonNull(name);
    this.score = score;
  }

  /**
   * Static factory method to create a new instance of {@code PlayerDTO} with the specified 
   * {@code name} and {@code score}.
   *
   * @param name the name of the player.
   * @param score the score of the player.
   * @return a new {@code PlayerDTO} instance containing {@code name} and {@code score}.
   */
  public static PlayerDTO newInstance(String name, int score) {
    return new PlayerDTO(name, score);
  }

  /**
   * Returns the name of the player.
   *
   * @return the name of the player.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the score of the player.
   *
   * @return the score of the player.
   */
  public int getScore() {
    return score;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, score);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    PlayerDTO playerDTO = (PlayerDTO) obj;
    return score == playerDTO.score && Objects.equals(name, playerDTO.name);
  }
}
