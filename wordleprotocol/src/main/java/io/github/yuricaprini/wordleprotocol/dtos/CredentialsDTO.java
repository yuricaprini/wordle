package io.github.yuricaprini.wordleprotocol.dtos;

import java.util.Objects;

/**
 * A {@code CredentialsDTO} represents an immutable data transfer object used to hold user
 * credentials, consisting of a username and a password. 
 */
public class CredentialsDTO {

  private final String username;
  private final String password;

  /**
   * Static factory method to create a new instance of {@code CredentialsDTO} with the specified 
   * {@code username} and {@code password}.
   *
   * @param username the user username.
   * @param password the user password.
   * @return a {@code CredentialsDTO} instance containing {@code username} and {@code password}.
   * @throws NullPointerException if {@code username == null || password == null }
   */
  public static CredentialsDTO newInstance(String username, String password) {
    return new CredentialsDTO(Objects.requireNonNull(username), Objects.requireNonNull(password));
  }

  /**
   * Private constructor to create a {@code CredentialsDTO} object with the specified 
   * {@code username} and {@code password}.
   *
   * @param username the user username.
   * @param password the user password.
   */
  private CredentialsDTO(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Returns the user username.
   *
   * @return the user username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Returns the user password.
   *
   * @return the user password.
   */
  public String getPassword() {
    return password;
  }
}
