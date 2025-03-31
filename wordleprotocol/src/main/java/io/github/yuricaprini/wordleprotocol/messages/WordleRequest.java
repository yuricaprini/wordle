package io.github.yuricaprini.wordleprotocol.messages;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadRequestException;
import io.github.yuricaprini.wordleprotocol.exceptions.RequestFullyPopulatedException;
import io.github.yuricaprini.wordleprotocol.exceptions.RequestTooLargeException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

/**
 * An {@code WordleRequest} represents a request made by a client to Wordle server.
 * This interface also provides methods for populating an empty request from an input queue and 
 * serializing it to an output queue.
 */
public interface WordleRequest {

  /**
  * The maximum size in bytes allowed for a Wordle request.
  */
  public static final int MAX_SIZE = 1 * 1024; //1 kB

  /**
   * Populates an empty request by parsing the bytes from the specified {@code inputQueue}.
   * If the {@code inputQueue} contains a partial request, this wordle request is partially
   * populated and its state is preserved for future calls to this method.
   *
   * @param inputQueue the input queue containing the request in bytes.
   * @return {@code true} if the request is fully populated, or {@code false} if it's partially
   * populated and requires more data in future calls.
   * @throws NullPointerException if {@code inputQueue == null}
   * @throws RequestFullyPopulatedException if this request is fully populated.
   * @throws RequestTooLargeException if the request size exceeds {@link #MAX_SIZE}.
   * @throws BadRequestException if the request data is malformed or invalid.
   */
  public boolean populateFrom(InputQueue inputQueue)
      throws RequestTooLargeException, BadRequestException;

  /**
   * Serializes the request and writes it to the specified {@code outputQueue}.
   * If this request is not fully populated this method has no effect and return false.
  
   * @param outputQueue the output queue to which the serialized request is written.
   * @throws NullPointerException if {@code outputQueue == null}
   * @return {@code true} if the request is successfully serialized, {@code false} if the request
   * is not fully populated.
   */
  public boolean serializeTo(OutputQueue outputQueue);

  /**
   * Checks if the request is fully populated.
   *
   * @return {@code true} if the request is fully populated, {@code false} otherwise.
   */
  public boolean isFullyPopulated();

  /**
   * Returns the {@code Type} of this request.
   *
   * @return the type of this request.
   */
  public WordleRequest.Type getType();

  /**
   * Returns the user authentication token.
   *
   * @return the user authentication token or {@code null} if no token is inside the request.
   */
  public AuthToken getAuthToken();

  /**
   * Returns the dto containing the user credentials.
   *
   * @return the dto containing the user credentials or {@code null} if no credentials are inside 
   * the request.
   */
  public CredentialsDTO getCredentialsDTO();

  /**
   * Returns the dto containing the word proposed by the user as an attempt to guess the word of 
   * the day.
   *
   * @return the dto containing the word proposed by the user or {@code null} if no word is inside 
   * the request.
   */
  public WordDTO getWordDTO();

  /**
   * Returns the dto containing the player indicating the starting point (excluded) of a requested 
   * ranking page.
   * @return the dto containing the player indicating the starting point (excluded) of a requested 
   * ranking page.
   */
  public PlayerDTO getPlayerDTO();

  /**
  * Enumerates the different types of Wordle requests.
  */
  public enum Type {
    LOGIN, PLAY_WORDLE, SEND_WORD, SHOWME_STATS, SHOWME_RANKING, SHARE
  }

  /**
   * The {@code Factory} interface defines methods for creating various types of Wordle requests.
   */
  public interface Factory {

    /**
     * Creates an empty Wordle request.
     *
     * @return an empty Wordle request.
     */
    public WordleRequest createEmptyRequest();

    /**
     * Creates a login Wordle request with the specified credentials.
     *
     * @param credentials the credentials data.
     * @return a login Wordle request.
     */
    public WordleRequest createLoginRequest(CredentialsDTO credentials);

    /**
     * Creates a Wordle request indicating that the user wants to start a new Wordle game.
     *
     * @param token the token needed to authenticate the user.
     * @return a Wordle request indicating that the user wants to start a new Wordle game.
     */
    public WordleRequest createPlayWordleRequest(AuthToken token);

    /**
     * Creates a Wordle request with the word proposed by the user as an attempt to guess the word
     * of the day.
     *
     * @param token the authentication token.
     * @param word the word proposed by the user.
     * @return a Wordle request with the word proposed by the user.
     */
    public WordleRequest createSendWordRequest(AuthToken token, WordDTO word);

    /**
      * Creates a Wordle request to retrieve user statistics.
      *
      * @param token the authentication token.
      * @return a request to retrieve user statistics.
     */
    public WordleRequest createShowMeStatsRequest(AuthToken token);

    /**
    * Creates a Wordle request to retrieve a page of the ranking, starting from the specified 
    * player (excluded). The ranking is retrieved in a bottom-up fashion.
    * If {@code player == null} the inital page from the player at bottom of the ranking (included)
    * is retrieved.
    *
    * @param authToken the authentication token of the user making the request.
    * @param player the player indicating the starting point (excluded) of the ranking page.
    *        If {@code null} the inital page from the player at bottom of the ranking (included) is 
    *        retrieved.
    * @return a Wordle request to retrieve a page of the ranking.
    */
    public WordleRequest createShowMeRankingRequest(AuthToken token, PlayerDTO player);

    /**
     * Creates a Wordle request to share user game results with other users.
     *
     * @param token the authentication token.
     * @return a Wordle request to share user game results with other users.
     */
    public WordleRequest createShareRequest(AuthToken authToken);
  }
}
