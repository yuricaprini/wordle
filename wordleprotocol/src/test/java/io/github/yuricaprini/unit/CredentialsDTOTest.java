package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;

public class CredentialsDTOTest {

  @Test
  public void shouldThrowExceptionIfUsernameOrPasswordIsNull() {
    assertThrows(NullPointerException.class, () -> {
      CredentialsDTO.newInstance(null, "password");
    });
    assertThrows(NullPointerException.class, () -> {
      CredentialsDTO.newInstance("username", null);
    });
    assertThrows(NullPointerException.class, () -> {
      CredentialsDTO.newInstance(null, null);
    });
  }

  @Test
  public void shouldBeOk() {
    String username = "username";
    String password = "password";
    CredentialsDTO credentialsDTO = CredentialsDTO.newInstance(username, password);

    assertEquals(username, credentialsDTO.getUsername());
    assertEquals(password, credentialsDTO.getPassword());
  }
}
