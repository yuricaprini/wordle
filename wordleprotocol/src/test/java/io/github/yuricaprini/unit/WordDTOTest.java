package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;

public class WordDTOTest {

  @Test
  public void shouldThrowExceptionIfWordIsNull() {
    assertThrows(NullPointerException.class, () -> {
      WordDTO.newInstance(null);
    });
  }

  @Test
  public void shouldBeOk() {
    String word = "word";
    WordDTO wordDTO = WordDTO.newInstance(word);
    assertEquals(word, wordDTO.getWord());
  }
}
