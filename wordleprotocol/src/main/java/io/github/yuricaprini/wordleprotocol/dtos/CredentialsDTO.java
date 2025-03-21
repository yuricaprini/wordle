package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Objects;

/**
 * A {@code CredentialsDTO} represents an immutable data transfer object used to hold user's
 * credentials, consisting of a username and a password. 
 */
public class CredentialsDTO {

  private final String username;
  private final String password;

  /**
   * Factory method to create a {@code CredentialsDTO} object with the specified {@code username}
   * and {@code password}.
   *
   * @param username the user's username.
   * @param password the user's password.
   * @return a {@code CredentialsDTO} object containing the specified credentials.
   * @throws NullPointerException if {@code username == null || password == null }
   */
  public static CredentialsDTO newInstance(String username, String password) {
    return new CredentialsDTO(Objects.requireNonNull(username), Objects.requireNonNull(password));
  }

  /**
   * Private constructor to create a {@code CredentialsDTO} object with the specified 
   * {@code username} and {@code password}.
   *
   * @param username the user's username.
   * @param password the user's password.
   */
  private CredentialsDTO(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Returns the user's username.
   *
   * @return the user's username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the user's password.
   *
   * @return the user's password.
   */
  public String getPassword() {
    return password;
  }
}
