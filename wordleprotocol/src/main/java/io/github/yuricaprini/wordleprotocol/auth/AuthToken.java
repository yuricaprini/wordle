package io.github.yuricaprini.wordleprotocol.auth;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import io.github.yuricaprini.wordleprotocol.exceptions.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * A {@code AuthToken} represents an authentication token used for user authentication.
 * It allows the creation of tokens with user-related claims and provides token verification.
 */
public class AuthToken implements Serializable {

  private final static String USERID_CLAME_NAME = "userID";
  private static String secretKey;
  private static int expirationTimeInterval;

  private final String token;

  /**
  * Initializes the secret key used for token signing and the tokens expiration interval.
  *
  * @param configSecretKey the secret key used for token signing.
  * @param expirationInterval the expiration interval for the tokens in milliseconds.
  * @throws NullPointerException if {@code configSecretKey} is {@code null}.
  * @throws IllegalArgumentException if {@code expirationInterval < 0 }
  */
  public static void init(String configSecretKey, int expirationInterval) {
    secretKey = Objects.requireNonNull(configSecretKey);
    if (expirationInterval < 0)
      throw new IllegalArgumentException();
    expirationTimeInterval = expirationInterval;
  }

  /**
   * Creates a new instance of {@code AuthToken} with the provided user ID as a claim and with 
   * expiration time equals to {@code now + expirationInterval} where {@code now} is the current 
   * date and {@code expirationInterval} is the expiration interval set at initialization.
   *
   * @param userID the user ID associated with the token as a claim.
   * @return a new {@code AuthToken} instance with the provided user ID as a claim.
   * @throws NullPointerException if {@code userID == null}.
   * @throws IllegalStateException if the secret key has not been initialized.
   */
  public static AuthToken newIstance(String userID) {

    if (secretKey == null)
      throw new IllegalStateException();

    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put(USERID_CLAME_NAME, Objects.requireNonNull(userID));

    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationTimeInterval);

    return new AuthToken(Jwts.builder().setClaims(claims).setIssuedAt(now)
        .setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, secretKey).compact());
  }

  /**
   * Wraps a string token into an {@code AuthToken}.
   *
   * @param token the string token to wrap.
   * @return a new AuthToken instance wrapping the provided token.
   */
  public static AuthToken wrap(String token) {
    return new AuthToken(token);
  }

  /**
   * Private constructor to create a {@code AuthToken} object wrapping the signed token.
   *
   * @param token the signed token.
   */
  private AuthToken(String token) {
    this.token = token;
  }

  /**
   * Validates the signature and the format of this token and return the associated user ID.
   *
   * @return the user ID associated with this token.
   * @throws IllegalStateException if the secret key has not been initialized.
   * @throws InvalidTokenException if the token is invalid or expired.
   */
  public String validate() throws InvalidTokenException {

    if (secretKey == null)
      throw new IllegalStateException();

    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
          .get(USERID_CLAME_NAME, String.class);

    } catch (Exception e) {
      throw new InvalidTokenException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;

    if (!(o instanceof AuthToken))
      return false;

    return this.token.equals(((AuthToken) o).token);
  }

  @Override
  public int hashCode() {
    return this.token.hashCode();
  }

  @Override
  public String toString() {
    return token;
  }
}
