package io.github.yuricaprini.wordleprotocol.messages;

import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
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
  public static final int MAX_SIZE = 1 * 1024; //1 kB

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
   * Retrieves the {@link #Type} of this response.
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
   * Enumeration representing different response types.
   */
  public enum Type {
    LOGIN_OK, LOGIN_NO, TOOLARGE, BAD
  }

  /**
   * Enumeration representing different error codes for WordleResponses.
   */
  public enum ErrorCode {
    USERNAME_SHORT, USERNAME_LONG, USERNAME_SPACE, PASSWORD_SHORT, PASSWORD_LONG, PASSWORD_SPACE, PASSWORD_NO_DIGIT, PASSWORD_NO_UC, NOT_REGISTERED_USER, INVALID_CREDENTIALS
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
  }
}
