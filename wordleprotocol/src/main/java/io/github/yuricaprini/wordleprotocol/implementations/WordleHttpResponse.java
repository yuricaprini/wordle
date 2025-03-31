package io.github.yuricaprini.wordleprotocol.implementations;

import static io.github.yuricaprini.wordleprotocol.implementations.BasicHttpMessage.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.dtos.GameStateDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.StatsDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadResponseException;
import io.github.yuricaprini.wordleprotocol.exceptions.MalformedHttpMessageException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseFullyPopulatedException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseTooLargeException;
import io.github.yuricaprini.wordleprotocol.exceptions.TooLongHttpMessageException;
import io.github.yuricaprini.wordleprotocol.implementations.BasicHttpMessage.ContentType;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;

/**
 * A {@code WordleHttpResponse} represents an HTTP response with Wordle-specific functionality. 
 * It can be populated from and serialized to HTTP messages. The class provides methods to parse
 * incoming HTTP messages and map them to Wordle-specific response, as well as to create and
 * serialize HTTP messages based on the Wordle response type.
 *
 * @see WordleResponse
 */
public class WordleHttpResponse implements WordleResponse {

  static final String RESPONSETYPE_CUSTOM_HKEY = "Response-Type";
  static final String ERRORCODE_CUSTOM_HKEY = "Error-Code";

  private Type type;
  private ErrorCode errorCode;
  private AuthToken authToken;
  private ClueDTO[] clues;
  private GameStateDTO gameState;
  private StatsDTO stats;
  private PlayerDTO[] players;

  private BasicHttpMessage httpMessage;
  private BasicHttpCodec httpCodec;
  private boolean isFullyPopulated;

  /**
   * Constructs a new {@code WordleHttpResponse} using a builder pattern.
   *
   * @param builder the builder used to construct the response.
   */
  private WordleHttpResponse(Builder builder) {
    this.type = builder.type;
    this.errorCode = builder.errorCode;
    this.authToken = builder.authToken;
    this.clues = builder.clues;
    this.gameState = builder.gameState;
    this.stats = builder.stats;
    this.players = builder.players;

    this.httpMessage = null;
    this.httpCodec = new BasicHttpCodec(MAX_SIZE);
    this.isFullyPopulated = builder.isFullyPopulated;
  }

  @Override
  public boolean populateFrom(InputQueue inputQueue)
      throws ResponseTooLargeException, BadResponseException {

    if (this.isFullyPopulated())
      throw new ResponseFullyPopulatedException();

    try {
      if ((httpMessage = httpCodec.decode(inputQueue)) == null)
        return false;
    } catch (MalformedHttpMessageException e) {
      throw new BadResponseException(e);
    } catch (TooLongHttpMessageException e) {
      throw new ResponseTooLargeException(e);
    }

    mapToWordleResponse(httpMessage);
    isFullyPopulated = true;

    return true;
  }

  @Override
  public boolean serializeTo(OutputQueue outputQueue) {

    if (isFullyPopulated) {
      httpMessage = mapToHttpMessage(this);
      httpCodec.encode(httpMessage, outputQueue);
      return true;
    }

    return false;
  }

  @Override
  public boolean isFullyPopulated() {
    return isFullyPopulated;
  }

  @Override
  public Type getType() {
    return this.type;
  }

  @Override
  public ErrorCode getErrorCode() {
    return this.errorCode;
  }

  @Override
  public AuthToken getAuthToken() {
    return this.authToken;
  }

  @Override
  public ClueDTO[] getCluesDTO() {
    return this.clues;
  }

  @Override
  public GameStateDTO getGameStateDTO() {
    return this.gameState;
  }

  @Override
  public StatsDTO getStatsDTO() {
    return this.stats;
  }

  @Override
  public PlayerDTO[] getPlayerDTOs() {
    return this.players;
  }

  /**
   * Private helper method to map {@code httpMessage} into a wordle response providing a semantic
   * validation (an {@code httpmessage} must have correctly set all and only the fields necessary 
   * to define a wordle response).
   * 
   * @param httpMessage the incoming HTTP message to be mapped.
   * @throws BadResponsetException if the HTTP message does not represents a wordle response. 
   */
  private void mapToWordleResponse(BasicHttpMessage httpMessage) throws BadResponseException {

    if (!httpMessage.getStartLineFirstToken().equals(VERSION))
      throw new BadResponseException();

    try {

      if (httpMessage.getHeaderValue(RESPONSETYPE_CUSTOM_HKEY) == null)
        throw new BadResponseException();

      switch (Type.valueOf(httpMessage.getHeaderValue(RESPONSETYPE_CUSTOM_HKEY))) {

        case LOGIN_OK:

          if (!httpMessage.getStartLineSecondToken().equals(SUCCESS_CODE)
              || !httpMessage.getStartLineThirdToken().equals(SUCCESS_DESC)
              || httpMessage.getHeaders().size() != 3
              || !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))

            throw new BadResponseException();

          authToken = new Gson().fromJson(httpMessage.getBody(), AuthToken.class);
          type = Type.LOGIN_OK;

          break;

        case LOGIN_NO:

          if (!httpMessage.getStartLineSecondToken().equals(UNAUTHORIZED_CODE)
              || !httpMessage.getStartLineThirdToken().equals(UNAUTHORIZED_DESC)
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY) == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY));
          type = Type.LOGIN_NO;

          break;

        case PLAYWORDLE_OK:

          if (!httpMessage.getStartLineSecondToken().equals(SUCCESS_CODE)
              || !httpMessage.getStartLineThirdToken().equals(SUCCESS_DESC))
            throw new BadResponseException();

          if (httpMessage.getHeaders().size() != 1 && httpMessage.getHeaders().size() != 3)
            throw new BadResponseException();

          if (httpMessage.getHeaders().size() == 3
              && !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadResponseException();

          if (httpMessage.getHeaders().size() == 3)
            clues = new Gson().fromJson(httpMessage.getBody(), ClueDTO[].class);

          type = Type.PLAYWORDLE_OK;

          break;

        case PLAYWORDLE_NO:

          if (!httpMessage.getStartLineSecondToken().equals(UNAUTHORIZED_CODE)
              || !httpMessage.getStartLineThirdToken().equals(UNAUTHORIZED_DESC)
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY) == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY));
          type = Type.PLAYWORDLE_NO;

          break;

        case SENDWORD_OK:
          if (!httpMessage.getStartLineSecondToken().equals(SUCCESS_CODE)
              || !httpMessage.getStartLineThirdToken().equals(SUCCESS_DESC)
              || httpMessage.getHeaders().size() != 3
              || !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadResponseException();

          gameState = new Gson().fromJson(httpMessage.getBody(), GameStateDTO.class);
          type = Type.SENDWORD_OK;

          break;

        case SENDWORD_NO:
          if (!httpMessage.getStartLineSecondToken().equals(UNAUTHORIZED_CODE)
              || !httpMessage.getStartLineThirdToken().equals(UNAUTHORIZED_DESC)
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY) == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY));
          type = Type.SENDWORD_NO;

          break;

        case SHOWMESTATS_OK:
          if (!httpMessage.getStartLineSecondToken().equals(SUCCESS_CODE)
              || !httpMessage.getStartLineThirdToken().equals(SUCCESS_DESC)
              || httpMessage.getHeaders().size() != 3
              || !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadResponseException();

          stats = new Gson().fromJson(httpMessage.getBody(), StatsDTO.class);
          type = Type.SHOWMESTATS_OK;

          break;

        case SHOWMESTATS_NO:
          if (!httpMessage.getStartLineSecondToken().equals(UNAUTHORIZED_CODE)
              || !httpMessage.getStartLineThirdToken().equals(UNAUTHORIZED_DESC)
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY) == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY));
          type = Type.SHOWMESTATS_NO;

          break;

        case SHOWMERANKING_OK:

          if (!httpMessage.getStartLineSecondToken().equals(SUCCESS_CODE)
              || !httpMessage.getStartLineThirdToken().equals(SUCCESS_DESC))
            throw new BadResponseException();

          if (httpMessage.getHeaders().size() != 1 && httpMessage.getHeaders().size() != 3)
            throw new BadResponseException();

          if (httpMessage.getHeaders().size() == 3
              && !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadResponseException();

          if (httpMessage.getHeaders().size() == 3)
            players = new Gson().fromJson(httpMessage.getBody(), PlayerDTO[].class);

          type = Type.SHOWMERANKING_OK;

          break;

        case SHOWMERANKING_NO:

          if (!httpMessage.getStartLineSecondToken().equals(UNAUTHORIZED_CODE)
              || !httpMessage.getStartLineThirdToken().equals(UNAUTHORIZED_DESC)
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY) == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY));
          type = Type.SHOWMERANKING_NO;

          break;

        case SHARE_OK:
          if (!httpMessage.getStartLineSecondToken().equals(SUCCESS_CODE)
              || !httpMessage.getStartLineThirdToken().equals(SUCCESS_DESC)
              || httpMessage.getHeaders().size() != 1)
            throw new BadResponseException();

          type = Type.SHARE_OK;

          break;

        case SHARE_NO:

          if (!httpMessage.getStartLineSecondToken().equals(UNAUTHORIZED_CODE)
              || !httpMessage.getStartLineThirdToken().equals(UNAUTHORIZED_DESC)
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY) == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue(ERRORCODE_CUSTOM_HKEY));
          type = Type.SHARE_NO;

          break;

        case BAD:

          if (!httpMessage.getStartLineSecondToken().equals(BADREQUEST_CODE)
              || !httpMessage.getStartLineThirdToken().equals(BADREQUEST_DESC)
              || httpMessage.getHeaders().size() != 1)
            throw new BadResponseException();

          type = Type.BAD;

          break;

        case TOOLARGE:

          if (!httpMessage.getStartLineSecondToken().equals(REQUESTTOOLARGE_CODE)
              || !httpMessage.getStartLineThirdToken().equals(REQUESTTOOLARGE_DESC)
              || httpMessage.getHeaders().size() != 1)
            throw new BadResponseException();

          type = Type.TOOLARGE;

          break;

      }
    } catch (JsonSyntaxException e) {
      throw new BadResponseException(e);
    } catch (IllegalArgumentException e) { //from Type conversion from string to enum
      throw new BadResponseException();
    }
  }

  /**
   * Private helper method to map a wordle response into a HTTP message.
   * 
   * @param wordleResponse the response to be mapped.
   * @return the HTTP message representing the given wordle response.
   */
  private BasicHttpMessage mapToHttpMessage(WordleHttpResponse httpResponse) {

    httpMessage = new BasicHttpMessage();

    switch (httpResponse.type) {

      case LOGIN_OK:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(SUCCESS_CODE);
        httpMessage.setStartLineThirdToken(SUCCESS_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.setBody(new Gson().toJson(authToken), ContentType.JSON);
        break;

      case LOGIN_NO:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(UNAUTHORIZED_CODE);
        httpMessage.setStartLineThirdToken(UNAUTHORIZED_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.addHeader(ERRORCODE_CUSTOM_HKEY, errorCode.toString());
        break;

      case PLAYWORDLE_OK:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(SUCCESS_CODE);
        httpMessage.setStartLineThirdToken(SUCCESS_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        if (clues != null)
          httpMessage.setBody(new Gson().toJson(clues), ContentType.JSON);
        break;

      case PLAYWORDLE_NO:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(UNAUTHORIZED_CODE);
        httpMessage.setStartLineThirdToken(UNAUTHORIZED_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.addHeader(ERRORCODE_CUSTOM_HKEY, errorCode.toString());
        break;

      case SENDWORD_OK:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(SUCCESS_CODE);
        httpMessage.setStartLineThirdToken(SUCCESS_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.setBody(new Gson().toJson(gameState), ContentType.JSON);
        break;

      case SENDWORD_NO:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(UNAUTHORIZED_CODE);
        httpMessage.setStartLineThirdToken(UNAUTHORIZED_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.addHeader(ERRORCODE_CUSTOM_HKEY, errorCode.toString());
        break;

      case SHOWMESTATS_OK:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(SUCCESS_CODE);
        httpMessage.setStartLineThirdToken(SUCCESS_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.setBody(new Gson().toJson(stats), ContentType.JSON);
        break;

      case SHOWMESTATS_NO:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(UNAUTHORIZED_CODE);
        httpMessage.setStartLineThirdToken(UNAUTHORIZED_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.addHeader(ERRORCODE_CUSTOM_HKEY, errorCode.toString());
        break;

      case SHOWMERANKING_OK:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(SUCCESS_CODE);
        httpMessage.setStartLineThirdToken(SUCCESS_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        if (players != null)
          httpMessage.setBody(new Gson().toJson(players), ContentType.JSON);
        break;

      case SHOWMERANKING_NO:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(UNAUTHORIZED_CODE);
        httpMessage.setStartLineThirdToken(UNAUTHORIZED_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.addHeader(ERRORCODE_CUSTOM_HKEY, errorCode.toString());
        break;

      case SHARE_OK:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(SUCCESS_CODE);
        httpMessage.setStartLineThirdToken(SUCCESS_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        break;

      case SHARE_NO:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(UNAUTHORIZED_CODE);
        httpMessage.setStartLineThirdToken(UNAUTHORIZED_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        httpMessage.addHeader(ERRORCODE_CUSTOM_HKEY, errorCode.toString());
        break;

      case BAD:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(BADREQUEST_CODE);
        httpMessage.setStartLineThirdToken(BADREQUEST_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        break;

      case TOOLARGE:
        httpMessage.setStartLineFirstToken(VERSION);
        httpMessage.setStartLineSecondToken(REQUESTTOOLARGE_CODE);
        httpMessage.setStartLineThirdToken(REQUESTTOOLARGE_DESC);
        httpMessage.addHeader(RESPONSETYPE_CUSTOM_HKEY, type.toString());
        break;
    }

    return httpMessage;
  }

  /**
  * The {@code Builder} class is used to construct instances of the {@link WordleHttpResponse} class
  * in a flexible and readable manner. It provides methods for setting various properties of the
  * response such as type, error code, authentication token, and whether the response is fully
  * populated. Once all desired properties are set, the build method creates and returns a
  * fully-configured WordleHttpResponse instance.
  */
  public static class Builder {

    private Type type;
    private ErrorCode errorCode;
    private AuthToken authToken;
    private ClueDTO[] clues;
    private GameStateDTO gameState;
    private StatsDTO stats;
    public PlayerDTO[] players;
    private boolean isFullyPopulated;

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder withErrorCode(ErrorCode errorCode) {
      this.errorCode = errorCode;
      return this;
    }

    public Builder withAuthToken(AuthToken authToken) {
      this.authToken = authToken;
      return this;
    }

    public Builder withClues(ClueDTO[] clues) {
      this.clues = clues;
      return this;
    }

    public Builder withGameState(GameStateDTO gameState) {
      this.gameState = gameState;
      return this;
    }

    public Builder withStats(StatsDTO stats) {
      this.stats = stats;
      return this;
    }

    public Builder withPlayers(PlayerDTO[] players) {
      this.players = players;
      return this;
    }

    public Builder withFullyPopulated(boolean isFullyPopulated) {
      this.isFullyPopulated = isFullyPopulated;
      return this;
    }

    public WordleHttpResponse build() {
      return new WordleHttpResponse(this);
    }
  }

}
