package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;

import java.util.HashMap;
import java.util.Map;

public class StatsDTOTest {

  @Test
  public void shouldThrowExceptionIfGuessDistributionIsNull() {
    assertThrows(NullPointerException.class, () -> {
      StatsDTO.newInstance(0, 0, 0, 0, null);
    });
  }

  @Test
  public void shouldBeOk() {
    int played = 10;
    int winPercentage = 70;
    int currentStreak = 3;
    int maxStreak = 5;

    Map<Integer, Integer> guessDistribution = new HashMap<>();
    guessDistribution.put(1, 2);
    guessDistribution.put(2, 5);
    guessDistribution.put(3, 3);

    StatsDTO statsDTO =
        StatsDTO.newInstance(played, winPercentage, currentStreak, maxStreak, guessDistribution);

    assertEquals(played, statsDTO.getPlayed());
    assertEquals(winPercentage, statsDTO.getWinPercentage());
    assertEquals(currentStreak, statsDTO.getCurrentStreak());
    assertEquals(maxStreak, statsDTO.getMaxStreak());
    assertEquals(guessDistribution, statsDTO.getGuessDistribution());
  }
}
