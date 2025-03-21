package io.github.yuricaprini.wordleprotocol.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * A {@code AuthToken} represents an authentication token used for user authentication.
 * It allows the creation of tokens with user-related claims and provides token verification.
 */
public class AuthToken {

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
   * @throws UnsupportedOperationException if the secret key has not been initialized.
   */
  public static AuthToken newIstance(String userID) {

    if (secretKey == null)
      throw new UnsupportedOperationException();

    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put(USERID_CLAME_NAME, Objects.requireNonNull(userID));

    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationTimeInterval);

    return new AuthToken(Jwts.builder().setClaims(claims).setIssuedAt(now)
        .setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, secretKey).compact());
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
   * Verifies the authenticity of the token or if it's expired and retrieves the associated user ID.
   *
   * @return the user ID associated with the token, or null if verification fails.
   * @throws UnsupportedOperationException if the secret key has not been initialized.
   */
  public String verify() {

    if (secretKey == null)
      throw new UnsupportedOperationException();

    try {
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
          .get(USERID_CLAME_NAME, String.class);

    } catch (Exception e) {
      return null;
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
