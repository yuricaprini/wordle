package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import io.github.yuricaprini.wordleprotocol.dtos.SecretWordDTO;

public class SecretWordDTOTest {

  @Test
  public void shouldBeOk() {
    Integer wordNumber = 1;
    String secretWord = "secretWord";
    String translatedSecretWord = "parolaSegreta";

    SecretWordDTO secretWordDTO =
        SecretWordDTO.newInstance(wordNumber, secretWord, translatedSecretWord);

    assertEquals(wordNumber, secretWordDTO.getWordNumber());
    assertEquals(secretWord, secretWordDTO.getSecretWord());
    assertEquals(translatedSecretWord, secretWordDTO.getTranslatedSecretWord());
  }
}
