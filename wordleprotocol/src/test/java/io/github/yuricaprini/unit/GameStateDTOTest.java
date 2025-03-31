package io.github.yuricaprini.unit;

import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.GameStateDTO;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.dtos.SecretWordDTO;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateDTOTest {

  @Test
  public void shouldBeOk() {
    ClueDTO[] clues = {ClueDTO.newInstance("word1", "++--"), ClueDTO.newInstance("word2", "+++")};
    SecretWordDTO secretWord = SecretWordDTO.newInstance(1, "secret", "translated");
    StatsDTO stats = StatsDTO.newInstance(5, 60, 2, 3, createGuessDistribution());

    GameStateDTO gameStateDTO = GameStateDTO.newInstance(clues, secretWord, stats);

    assertArrayEquals(clues, gameStateDTO.getCluesDTO());
    assertEquals(secretWord, gameStateDTO.getSecretWordDTO());
    assertEquals(stats, gameStateDTO.getStatsDTO());
  }

  private Map<Integer, Integer> createGuessDistribution() {
    Map<Integer, Integer> guessDistribution = new HashMap<>();
    guessDistribution.put(1, 2);
    guessDistribution.put(2, 3);
    guessDistribution.put(3, 1);
    return guessDistribution;
  }
}
