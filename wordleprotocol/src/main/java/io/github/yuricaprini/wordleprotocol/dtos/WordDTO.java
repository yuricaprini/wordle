package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Objects;

/**
 * A {@code WordDTO} represents an immutable data transfer object used to hold a user's guessed
 * word.
 */
public class WordDTO {

  private final String word;

  /**
   * Static factory method to create a new instance of {@code WordDTO} with the specified 
   * {@code word}.
   *
   * @param word the user's guessed word.
   * @return a new {@code WordDTO} instance containing the specified {@code word}.
   * @throws NullPointerException if {@code word == null}
   */
  public static WordDTO newInstance(String word) {
    return new WordDTO(Objects.requireNonNull(word));
  }

  /**
   * Private constructor to create a {@code WordDTO} object with the specified {@code word}.
   *
   * @param word the user's guessed word.
   */
  private WordDTO(String word) {
    this.word = word;
  }

  /**
   * Returns the user's guessed word.
   *
   * @return the user's guessed word.
   */
  public String getWord() {
    return word;
  }
}

