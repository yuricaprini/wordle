package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;

public class AuthTokenTest {

  private static final String SECRET_KEY = "mySecretKey";
  private static final String USER_ID = "myUserId";

  @Test
  public void tokenShouldBeExpired() {
    AuthToken.init(SECRET_KEY, 0);
    AuthToken authToken = AuthToken.newIstance(USER_ID);

    assertNull(authToken.verify());
  }

  @Test
  public void tokensShouldBeEqual() {
    AuthToken.init(SECRET_KEY, 15 * 60 * 1000);
    AuthToken authToken1 = AuthToken.newIstance(USER_ID);
    AuthToken authToken2 = authToken1;

    assertEquals(authToken1, authToken2);
    assertEquals(authToken1.hashCode(), authToken2.hashCode());
  }

  @Test
  public void tokensShouldBeDifferent() throws InterruptedException {
    AuthToken.init(SECRET_KEY, 15 * 60 * 1000);
    AuthToken authToken1 = AuthToken.newIstance(USER_ID);
    Thread.sleep(1000);
    AuthToken authToken2 = AuthToken.newIstance(USER_ID);
    AuthToken authToken3 = AuthToken.newIstance("differebt");

    assertNotEquals(authToken1, authToken2);
    assertNotEquals(authToken2, authToken3);
    assertNotEquals(authToken1.hashCode(), authToken2.hashCode());
  }

  @Test
  public void flowShouldBeOK() {
    AuthToken.init(SECRET_KEY, 15 * 60 * 1000);
    AuthToken authToken = AuthToken.newIstance(USER_ID);
    String userID = authToken.verify();

    assertEquals(USER_ID, userID);
  }
}

