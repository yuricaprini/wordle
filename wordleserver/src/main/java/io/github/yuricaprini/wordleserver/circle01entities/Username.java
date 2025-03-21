package io.github.yuricaprini.wordleserver.circle01entities;

import java.util.Objects;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.SpaceInUsernameException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooLongUsernameException;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.TooShortUsernameException;

/**
 * A {@code Username} is a string without spaces, long at least {@value #MIN_LEN} characters and no
 * more than {@value #MAX_LEN} characters.
 * 
 * @author Yuri Caprini
 */
public class Username {

  public static final int MIN_LEN = 4;
  public static final int MAX_LEN = 10;

  private String value;

  /**
   * Creates a new instance of {@code Username} from {@code value}.
   * 
   * @param value the string value of the newly created {@code Username}.
   * @throws NullPointerException if {@code value == null}.
   * @throws TooShortUsernameException  if {@code value.length()} is lower than {@value #MIN_LEN}.
   * @throws TooLongUsernameException if {@code value.length()} is bigger than {@value #MAX_LEN}.
   * @throws SpaceInUsernameException if {@code value} contains at least one space.
   */
  public Username(String value)
      throws TooShortUsernameException, TooLongUsernameException, SpaceInUsernameException {

    Objects.requireNonNull(value);

    if (value.length() < MIN_LEN)
      throw new TooShortUsernameException();

    if (value.length() > MAX_LEN)
      throw new TooLongUsernameException();

    if (value.contains(" "))
      throw new SpaceInUsernameException();

    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    return this.value.equals(((Username) o).toString());
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
