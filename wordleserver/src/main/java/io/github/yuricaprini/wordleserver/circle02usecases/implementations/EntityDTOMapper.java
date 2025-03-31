package io.github.yuricaprini.wordleserver.circle02usecases.implementations;

import java.util.ArrayList;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.dtos.GameStateDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.SecretWordDTO;
import io.github.yuricaprini.wordleprotocol.dtos.SharedGameResultDTO;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;
import io.github.yuricaprini.wordleserver.circle01entities.Clue;
import io.github.yuricaprini.wordleserver.circle01entities.Score;
import io.github.yuricaprini.wordleserver.circle01entities.SecretWord;
import io.github.yuricaprini.wordleserver.circle01entities.Stats;
import io.github.yuricaprini.wordleserver.circle01entities.Username;
import io.github.yuricaprini.wordleserver.circle01entities.Word;
import io.github.yuricaprini.wordleserver.circle01entities.exceptions.IllegalWordLengthException;

public class EntityDTOMapper {

  public static StatsDTO statsToDTO(Stats stats) {
    if (stats == null)
      return null;
    return StatsDTO.newInstance(stats.getPlayed(), stats.getWinPercentage(),
        stats.getCurrentStreak(), stats.getMaxStreak(), stats.getGuessDistribution());
  }

  public static PlayerDTO[] playersToDTOs(ArrayList<Pair<Username, Score>> players) {
    if (players.size() == 0)
      return null;

    PlayerDTO[] playerDTOs = new PlayerDTO[players.size()];
    int i = 0;
    for (Pair<Username, Score> p : players) {
      playerDTOs[i] = PlayerDTO.newInstance(p.getValue0().toString(), p.getValue1().getValue());
      i++;
    }
    return playerDTOs;
  }

  public static SharedGameResultDTO gameResultToDTO(Username username, SecretWord secretWord,
      Clue[] clues) {
    String[] cluesColors = new String[clues.length];
    for (int i = 0; i < cluesColors.length; i++) {
      cluesColors[i] = clues[i].getWordColors();
    }
    return SharedGameResultDTO.newInstance(username.toString(), secretWord.getNumber(),
        cluesColors);
  }

  /**
  * Converts a {@link Clue} array to a {@link ClueDTO} array.
  *
  * @param clues the array of clues to be converted.
  * @return a {@code ClueDTO} array or {@code null} if {@code clues == null}
  */
  public static ClueDTO[] cluesToDTO(Clue[] clues) {
    if (clues.length == 0)
      return null;

    ClueDTO[] clueDTOs = new ClueDTO[clues.length];
    for (int i = 0; i < clueDTOs.length; i++) {
      clueDTOs[i] = ClueDTO.newInstance(clues[i].getWord().getValue(), clues[i].getWordColors());
    }
    return clueDTOs;
  }


  public static SecretWordDTO secretWordToDTO(SecretWord secretWord) {
    if (secretWord == null)
      return null;
    return SecretWordDTO.newInstance(secretWord.getNumber(), secretWord.getValue(),
        secretWord.getTranslation());
  }

  public static Word convertToWord(WordDTO wordDTO) throws IllegalWordLengthException {
    return new Word(wordDTO.getWord());
  }

  public static GameStateDTO gameStateToDTO(Triplet<Clue[], SecretWord, Stats> gameState) {

    ClueDTO[] clueDTOs = cluesToDTO(gameState.getValue0());
    SecretWordDTO secretWordDTO = secretWordToDTO(gameState.getValue1());
    StatsDTO statsDTO = statsToDTO(gameState.getValue2());

    return GameStateDTO.newInstance(clueDTOs, secretWordDTO, statsDTO);
  }
}
