package io.github.yuricaprini.wordleserver.circle01entities;

import io.github.yuricaprini.wordleserver.circle01entities.exceptions.IllegalWordLengthException;

/**
 * The {@code Word} class represents a word in the Wordle game of exactly {@value #FIXED_LEN}
 * letters.
 * 
 * @author Yuri Caprini
 */
public class Word {

  public static final int FIXED_LEN = 10;
  protected String value;

  /**
  * Constructs a new {@code Word} with the specified string value.
  * 
  * @param value the string value of the word.
  * @throws IllegalWordLengthException if the provided word does not have the expected fixed 
  * length.
  */
  public Word(String value) throws IllegalWordLengthException {
    if (value.length() != FIXED_LEN)
      throw new IllegalWordLengthException();
    this.value = value;
  }

  /**
  * Gets the string value of the word.
  * 
  * @return the string value of the word.
  */
  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    Word otherWord = (Word) obj;
    return value.equals(otherWord.value);
  }
}
