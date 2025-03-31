package io.github.yuricaprini.unit;

import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerDTOTest {

  @Test
  public void shouldThrowExceptionIfNameIsNull() {
    assertThrows(NullPointerException.class, () -> {
      PlayerDTO.newInstance(null, 100);
    });
  }

  @Test
  public void shouldBeOk() {
    String playerName = "John";
    int playerScore = 150;

    PlayerDTO playerDTO = PlayerDTO.newInstance(playerName, playerScore);

    assertEquals(playerName, playerDTO.getName());
    assertEquals(playerScore, playerDTO.getScore());
  }
}
