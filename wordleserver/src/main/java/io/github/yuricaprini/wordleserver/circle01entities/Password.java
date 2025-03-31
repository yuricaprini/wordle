package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.SpaceInPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.NoDigitPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.NoUppercasePasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooLongPasswordException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooShortPasswordException;

/**
 * A {@code Password} is a string which contains at least one digit and one uppercase letter, long * at least {@value #MIN_LEN} characters and no more than {@value #MAX_LEN} characters.
 * 
 * @author Yuri Caprini
 */
public class Password {

  public static final int MIN_LEN = 8;
  public static final int MAX_LEN = 16;

  private String value;

  /**
  * Creates a new instance of {@code Password} with the specified value.
  *
  * @param value the string value of the password.
  * @throws NullPointerException if {@code value} is {@code null}.
  * @throws TooShortPasswordException if {@code value.length()} is lower than {@value #MIN_LEN}.
  * @throws TooLongPasswordException if {@code value.length()} is bigger than {@value #MAX_LEN}.
  * @throws SpaceInPasswordException if {@code value} contains at least one space.
  * @throws NoDigitPasswordException if {@code value} lacks a digit.
  * @throws NoUppercasePasswordException if {@code value} lacks an uppercase letter.
  */
  public Password(String value) throws TooShortPasswordException, TooLongPasswordException,
      SpaceInPasswordException, NoDigitPasswordException, NoUppercasePasswordException {

    if (value == null)
      throw new NullPointerException();

    if (value.length() < MIN_LEN)
      throw new TooShortPasswordException();

    if (value.length() > MAX_LEN)
      throw new TooLongPasswordException();

    if (value.contains(" "))
      throw new SpaceInPasswordException();

    Pattern pattern = Pattern.compile("[0-9]");
    Matcher matcher = pattern.matcher(value);
    if (!matcher.find())
      throw new NoDigitPasswordException();

    pattern = Pattern.compile("[A-Z]");
    matcher = pattern.matcher(value);
    if (!matcher.find())
      throw new NoUppercasePasswordException();

    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    return this.value.equals(((Password) o).value);
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }

  @Override
  public String toString() {
    return this.value;
  }
}
