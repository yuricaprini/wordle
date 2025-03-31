package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.Objects;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.IllegalWordLengthException;

/**
 * A {@code SecretWord} is the {@code Word} to guess, chosen by Wordle periodically.
 * 
 * @author Yuri Caprini
 */
public class SecretWord extends Word {

  public static final String DEFAULT_SECRETWORD = "abalienate";
  public static final String DEFAULT_TRANSLATION = "abalienare";
  private static SecretWord instance;
  private int number;
  private String translation;

  /**
   * Private constructor for the {@code SecretWord} class.
   * Initializes the instance with the default secret word and sets the number to 0.
   */
  private SecretWord() {
    super(DEFAULT_SECRETWORD);
    this.number = 0;
    this.translation = DEFAULT_TRANSLATION;
  }

  /**
   * Sets a new value for the {@code SecretWord}.
   *
   * @param value the new value to set.
   * @param translation the tranlated secret word.
   * @throws NullPointerException if {@code value==null}
   * @throws IllegalWordLengthException if {@code value.length()== }{@value #FIXED_LEN}.
   */
  public synchronized static void setNew(String value, String translation) {

    if (Objects.requireNonNull(value).length() != FIXED_LEN)
      throw new IllegalWordLengthException();

    if (instance == null)
      instance = new SecretWord();

    instance.number++;
    instance.value = value;
    instance.translation = translation;
  }

  /**
   * Creates a copy of the current {@code SecretWord} instance.
   *
   * @return a new instance of {@code SecretWord} with the same values.
   */
  public synchronized static SecretWord getCopy() {
    if (instance == null)
      instance = new SecretWord();

    SecretWord copy = new SecretWord();
    copy.number = instance.number;
    copy.value = instance.value;
    copy.translation = instance.translation;

    return copy;
  }

  /**
   * Gets the number of this {@code SecretWord}.
   *
   * @return the number of this {@code SecretWord}.
   */
  public int getNumber() {
    return number;
  }

  /**
   * Get the translated secret word.
   * 
   * @return the translated secret word.
   */
  public String getTranslation() {
    return translation;
  }
}
