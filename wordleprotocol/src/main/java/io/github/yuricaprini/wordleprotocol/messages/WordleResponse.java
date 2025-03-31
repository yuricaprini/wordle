package io.github.yuricaprini.wordleprotocol.messages;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.dtos.GameStateDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadResponseException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseFullyPopulatedException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseTooLargeException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

/**
 * A {@code WordleResponse} represents a response sent by the Wordle server to a client's request.
 * This interface provides methods for populating an empty response from an input queue and
 * serializing it to an output queue.
 */
public interface WordleResponse {

  /**
  * The maximum size in bytes allowed for a Wordle response.
  */
  public static final int MAX_SIZE = 2 * 1024; // kB

  /**
   * Populates an empty response by parsing the bytes from the specified {@code inputQueue}.
   * If the {@code inputQueue} contains a partial response, this Wordle response is partially
   * populated and its state is preserved for future calls to this method.
   *
   * @param inputQueue the input queue containing the response in bytes.
   * @return {@code true} if the response is fully populated, or {@code false} if it's partially
   * populated and requires more data in future calls.
   * @throws NullPointerException if {@code inputQueue == null}
   * @throws ResponseFullyPopulatedException if this response is fully populated.
   * @throws ResponseTooLargeException if the response size exceeds {@link #MAX_SIZE}.
   * @throws BadResponseException if the response data is malformed or invalid.
   */
  public boolean populateFrom(InputQueue inputQueue)
      throws ResponseTooLargeException, BadResponseException;

  /**
  * Serializes the response and writes it to the specified {@code outputQueue}.
  * If this response is not fully populated this method has no effect and return false.
  *
  * @param outputQueue the output queue to which the serialized response is written.
  * @throws NullPointerException if {@code outputQueue == null}
  * @return {@code true} if the response is successfully serialized, {@code false} if the response
  * is not fully populated.
  */
  public boolean serializeTo(OutputQueue outputQueue);

  /**
   * Checks if the response is fully populated.
   *
   * @return {@code true} if the response is fully populated, {@code false} otherwise.
   */
  public boolean isFullyPopulated();

  /**
   * Retrieves the {@code Type} of this response.
   *
   * @return the type of this response.
   */
  public Type getType();

  /**
   * Gets the {@link ErrorCode} associated with this response.
   *
   * @return the error code, which is one of the enumerated values in {@link ErrorCode}.
   */
  public ErrorCode getErrorCode();

  /**
   * Retrieves the user authentication token.
   *
   * @return the user authentication token or {@code null} if no token is inside the response.
   */
  public AuthToken getAuthToken();

  /**
   * Returns the dtos containing the clues given for the game in progress.
   *
   * @return the dto containing clues given for the game in progress or {@code null} if no clue dtos
   * are inside this response.
   */
  public ClueDTO[] getCluesDTO();

  /**
   * Return the dto containing the game state.
   * 
   * @return the dto containing the game state or {@code null} if no clue dtos are inside this 
   * response.
   */
  public GameStateDTO getGameStateDTO();

  /**
   * Return the dto containing user stats.
   * 
   * @return the dto containing user stats or {@code null} if no clue dtos are inside this response.
   */
  public StatsDTO getStatsDTO();

  /**
  * Return the DTOs containing player information
  * 
  * @return the DTOs containing player information or {@code null} if no clue dtos are inside this
  * response.
  */
  public PlayerDTO[] getPlayerDTOs();

  /**
   * Enumeration representing different response types.
   */
  public enum Type {
    //@formatter:off
    LOGIN_OK, LOGIN_NO, TOOLARGE, BAD, PLAYWORDLE_OK, PLAYWORDLE_NO, SENDWORD_OK, SENDWORD_NO,
    SHOWMESTATS_OK, SHOWMESTATS_NO, SHOWMERANKING_OK, SHOWMERANKING_NO, SHARE_OK, SHARE_NO
    //@formatter:on

  }

  public enum ErrorCode {
    //@formatter:off
    USERNAME_SHORT, USERNAME_LONG, USERNAME_SPACE, PASSWORD_SHORT, PASSWORD_LONG, PASSWORD_SPACE, 
    PASSWORD_NO_DIGIT, PASSWORD_NO_UC, NOT_REGISTERED_USER, INVALID_CREDENTIALS, INVALID_AUTHTOKEN, 
    GAME_ALREADY_PLAYED, GAME_NOT_STARTED, ILLEGAL_WORD_VOCABULARY, ILLEGAL_WORD_LENGTH, 
    INVALID_CURSOR, INTERNAL_ERROR, NO_GAMES_PLAYED
    //@formatter:on
  }

  /**
   * The {@code Factory} interface defines methods for creating various types of Wordle responses.
   */
  public interface Factory {

    /**
     * Creates an empty Wordle response.
     *
     * @return an empty Wordle response.
     */
    public WordleResponse createEmptyResponse();

    /**
     * Creates a Wordle response representing a successful login.
     *
     * @param authToken the authentication token resulting from the successfull login.
     * @return a Wordle response representing a successful login.
     */
    public WordleResponse createLoginOK(AuthToken authToken);

    /**
     * Creates a Wordle response representing a failed login with a specific error code.
     *
     * @param errorCode the error code associated with the failed login.
     * @return a Wordle response representing a failed login.
     */
    public WordleResponse createLoginNO(ErrorCode errorCode);

    /**
     * Create a Wordle response indicating that a Wordle game is in progress for the user or, in 
     * case {@code clues == null}, that a new game has started.
     * 
     * @param clueDTOs the clues so far provided to the user for the game in progress.
     * @return a Wordle response indicating that a Wordle game is in progress for the user or, in 
     * case {@code clues == null}, that a new game has started.
     */
    public WordleResponse createPlayWordleOK(ClueDTO[] clueDTOs);

    /**
     * Creates a Wordle response indicating a failed user request to play a new Wordle game.
     * 
     * @param errorCode the error code associated with the failed request to play a new Wordle game.
     * @return a Wordle response indicating a failed user request to play a new Wordle game.
     */
    public WordleResponse createPlayWordleNO(ErrorCode errorCode);

    /**
     * Creates a Wordle response indicating that the user has successfully attempted to guess the
     * secret word of the day, along with the information on the current game state.
     * 
     * @param gameStateDTO the dto containing the current game state 
     * @return a Wordle response indicating that the user has successfully attempted to guess the
     * secret word of the day, along with the information on the current game state.
     */
    public WordleResponse createSendWordOK(GameStateDTO gameStateDTO);

    /**
     * Creates a Wordle response indicating that the user attempt to guess the secret word of the
     * day has failed.
     * 
     * @param errorCode the error code associated with the failed attempt to guess the secret word 
     * of the day.
     * @return a Wordle response indicating that the user attempt to guess the secret word of the
     * day has failed.
     */
    public WordleResponse createSendWordNO(ErrorCode errorCode);

    /**
     * Creates a response indicating that a user request to retrieve their stats was successful.
     * 
     * @param statsDTO the dto containing user stats.
     * @return a response indicating that a user request to retrieve their stats was successful.
     */
    public WordleResponse createShowMeStatsOK(StatsDTO statsToDTO);

    /**
     * Creates a response indicating that a user request to retrieve their stats has failed.
     * 
     * @param errorCode the error code indicating the reason for the request failure.
     * @return a response indicating that a user request to retrieve their stats has failed.
     */
    public WordleResponse createShowMeStatsNO(ErrorCode errorCode);

    /**
    * Creates a response indicating that a user request to retrieve to retrieve a ranking page 
    * was successful.
    * 
    * @param playerDTOs the DTOs composing the ranking page.
    * @return a response indicating that a user request to retrieve to retrieve a ranking page 
    * was successful.
    */
    public WordleResponse createShowMeRankingOK(PlayerDTO[] playerDTOs);

    /**
     * Creates a response indicating that a user request to retrieve a ranking page has failed.
     * 
     * @param errorCode the error code indicating the reason for the request failure.
     * @return a response indicating that a user request to retrieve a ranking page has failed.
     */
    public WordleResponse createShowMeRankingNO(ErrorCode errorCode);

    /**
     * Creates a Wordle response indicating that the request is too large.
     *
     * @return a Wordle response indicating that the request is too large.
     */
    public WordleResponse createTooLargeRequest();

    /**
     * Creates a Wordle response indicating that the request is malformed or invalid.
     *
     * @return a Wordle response indicating that the request is malformed or invalid.
     */
    public WordleResponse createBadRequest();

    public WordleResponse createShareOK();

    public WordleResponse createShareNO(ErrorCode invalidAuthtoken);
  }

}
