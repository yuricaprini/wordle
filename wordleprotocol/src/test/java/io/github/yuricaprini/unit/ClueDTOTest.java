package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;

public class ClueDTOTest {

  @Test
  public void shouldThrowExceptionIfWordIsNull() {
    assertThrows(NullPointerException.class, () -> {
      ClueDTO.newInstance(null, "+?XX+X");
    });
  }

  @Test
  public void shouldThrowExceptionIfWordColorsIsNull() {
    assertThrows(NullPointerException.class, () -> {
      ClueDTO.newInstance("example", null);
    });
  }

  @Test
  public void shouldBeOk() {
    String word = "example";
    String wordColors = "+?XX+X";

    ClueDTO clueDTO = ClueDTO.newInstance(word, wordColors);

    assertEquals(word, clueDTO.getWord());
    assertEquals(wordColors, clueDTO.getWordColors());
  }
}
