package io.github.yuricaprini.wordleprotocol.implementations;

import static io.github.yuricaprini.wordleprotocol.implementations.BasicHttpMessage.*;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.PlayerDTO;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadRequestException;
import io.github.yuricaprini.wordleprotocol.exceptions.MalformedHttpMessageException;
import io.github.yuricaprini.wordleprotocol.exceptions.RequestFullyPopulatedException;
import io.github.yuricaprini.wordleprotocol.exceptions.RequestTooLargeException;
import io.github.yuricaprini.wordleprotocol.exceptions.TooLongHttpMessageException;
import io.github.yuricaprini.wordleprotocol.implementations.BasicHttpMessage.ContentType;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;

/**
 * A {@code WordleHttpRequest} represents an HTTP request with Wordle-specific functionality. 
 * It can be populated from and serialized to HTTP messages. The class provides methods to parse
 * incoming HTTP messages and map them to Wordle-specific requests, as well as to create and
 * serialize HTTP messages based on the Wordle request type.
 *
 * @see WordleRequest
 */
public class WordleHttpRequest implements WordleRequest {

  public static final String LOGIN_ENDPOINT = "/login";
  public static final String PLAYWORDLE_ENDPOINT = "/playwordle";
  private static final String SENDWORD_ENDPOINT = "/sendword";
  private static final String SHOWMESTATS_ENDPOINT = "/showmestats";
  private static final String SHOWMERANKING_ENDPOINT = "/showmeranking";
  private static final String SHARE_ENDPOINT = "/share";

  private Type type;
  private AuthToken authToken;
  private CredentialsDTO credentials;
  private WordDTO word;
  private PlayerDTO player;

  private BasicHttpMessage httpMessage;
  private BasicHttpCodec httpCodec;
  private boolean isFullyPopulated;

  /**
   * Constructs a new {@code WordleHttpRequest} using a builder pattern.
   *
   * @param builder the builder used to construct the request.
   */
  private WordleHttpRequest(Builder builder) {
    this.type = builder.type;
    this.authToken = builder.authToken;
    this.credentials = builder.credentials;
    this.word = builder.word;
    this.player = builder.player;

    this.httpMessage = null;
    this.httpCodec = new BasicHttpCodec(MAX_SIZE);
    this.isFullyPopulated = builder.isFullyPopulated;
  }

  @Override
  public boolean populateFrom(InputQueue inputQueue)
      throws RequestTooLargeException, BadRequestException {

    Objects.requireNonNull(inputQueue);

    if (this.isFullyPopulated())
      throw new RequestFullyPopulatedException();

    try {

      httpMessage = httpCodec.decode(inputQueue);

    } catch (MalformedHttpMessageException e) {
      throw new BadRequestException(e);
    } catch (TooLongHttpMessageException e) {
      throw new RequestTooLargeException(e);
    }

    if (httpMessage == null)
      return false;

    mapToWordleRequest(httpMessage);
    isFullyPopulated = true;

    return true;
  }

  @Override
  public boolean serializeTo(OutputQueue outputQueue) {

    if (!isFullyPopulated)
      return false;

    httpMessage = mapToHttpMessage(this);
    httpCodec.encode(httpMessage, outputQueue);
    return true;
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
  public AuthToken getAuthToken() {
    return this.authToken;
  }

  @Override
  public CredentialsDTO getCredentialsDTO() {
    return this.credentials;
  }

  @Override
  public WordDTO getWordDTO() {
    return this.word;
  }

  @Override
  public PlayerDTO getPlayerDTO() {
    return this.player;
  }

  /**
   * Private helper method to map {@code httpMessage} into a wordle request providing a semantic
   * validation (an {@code httpmessage} must have correctly set all and only the fields necessary 
   * to define a wordle request).
   * 
   * @param httpMessage the incoming HTTP message to be mapped.
   * @throws BadRequestException if the HTTP message does not represents a wordle request.
   */
  private void mapToWordleRequest(BasicHttpMessage httpMessage) throws BadRequestException {

    if (!httpMessage.getStartLineThirdToken().equals(VERSION))
      throw new BadRequestException();

    try {

      switch (httpMessage.getStartLineFirstToken() + SP + httpMessage.getStartLineSecondToken()) {

        case POST + SP + LOGIN_ENDPOINT:

          if (httpMessage.getHeaders().size() != 2
              || !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadRequestException();

          credentials = new Gson().fromJson(httpMessage.getBody(), CredentialsDTO.class);
          type = Type.LOGIN;

          break;

        case POST + SP + PLAYWORDLE_ENDPOINT:

          if (httpMessage.getHeaders().size() != 1 || httpMessage.getHeaderValue(AUTH_HKEY) == null)
            throw new BadRequestException();

          authToken = AuthToken.wrap(httpMessage.getHeaderValue(AUTH_HKEY).split(SP, 2)[1]);
          type = Type.PLAY_WORDLE;

          break;

        case POST + SP + SENDWORD_ENDPOINT:

          if (httpMessage.getHeaders().size() != 3 || httpMessage.getHeaderValue(AUTH_HKEY) == null
              || !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadRequestException();

          authToken = AuthToken.wrap(httpMessage.getHeaderValue(AUTH_HKEY).split(SP, 2)[1]);
          word = new Gson().fromJson(httpMessage.getBody(), WordDTO.class);
          type = Type.SEND_WORD;

          break;

        case GET + SP + SHOWMESTATS_ENDPOINT:

          if (httpMessage.getHeaders().size() != 1 || httpMessage.getHeaderValue(AUTH_HKEY) == null)
            throw new BadRequestException();

          authToken = AuthToken.wrap(httpMessage.getHeaderValue(AUTH_HKEY).split(SP, 2)[1]);
          type = Type.SHOWME_STATS;

          break;

        case GET + SP + SHOWMERANKING_ENDPOINT:

          if (httpMessage.getHeaders().size() != 1 && httpMessage.getHeaders().size() != 3)
            throw new BadRequestException();

          if (httpMessage.getHeaderValue(AUTH_HKEY) == null)
            throw new BadRequestException();

          if (httpMessage.getHeaders().size() == 3
              && !CONTENTTYPE_JSON_HVAL.equals(httpMessage.getHeaderValue(CONTENTTYPE_HKEY)))
            throw new BadRequestException();

          if (httpMessage.getHeaders().size() == 3)
            player = new Gson().fromJson(httpMessage.getBody(), PlayerDTO.class);

          authToken = AuthToken.wrap(httpMessage.getHeaderValue(AUTH_HKEY).split(SP, 2)[1]);
          type = Type.SHOWME_RANKING;

          break;

        case POST + SP + SHARE_ENDPOINT:

          if (httpMessage.getHeaders().size() != 1 || httpMessage.getHeaderValue(AUTH_HKEY) == null)
            throw new BadRequestException();

          authToken = AuthToken.wrap(httpMessage.getHeaderValue(AUTH_HKEY).split(SP, 2)[1]);
          type = Type.SHARE;

          break;

        default:
          throw new BadRequestException();
      }
    } catch (JsonSyntaxException e) {
      throw new BadRequestException(e);
    }
  }

  /**
   * Private helper method to map a wordle request into a HTTP message.
   * 
   * @param wordleRequest the request to be mapped.
   * @return the HTTP message representing the given wordle request.
   */
  private BasicHttpMessage mapToHttpMessage(WordleHttpRequest wordleRequest) {

    BasicHttpMessage httpMessage = new BasicHttpMessage();

    switch (wordleRequest.type) {

      case LOGIN:
        httpMessage.setStartLineFirstToken(POST);
        httpMessage.setStartLineSecondToken(LOGIN_ENDPOINT);
        httpMessage.setStartLineThirdToken(VERSION);
        httpMessage.setBody(new Gson().toJson(credentials), ContentType.JSON);
        break;

      case PLAY_WORDLE:
        httpMessage.setStartLineFirstToken(POST);
        httpMessage.setStartLineSecondToken(PLAYWORDLE_ENDPOINT);
        httpMessage.setStartLineThirdToken(VERSION);
        httpMessage.addHeader(AUTH_HKEY, BEARER_HVAL + SP + authToken.toString());
        break;

      case SEND_WORD:
        httpMessage.setStartLineFirstToken(POST);
        httpMessage.setStartLineSecondToken(SENDWORD_ENDPOINT);
        httpMessage.setStartLineThirdToken(VERSION);
        httpMessage.addHeader(AUTH_HKEY, BEARER_HVAL + SP + authToken.toString());
        httpMessage.setBody(new Gson().toJson(word), ContentType.JSON);
        break;

      case SHOWME_STATS:
        httpMessage.setStartLineFirstToken(GET);
        httpMessage.setStartLineSecondToken(SHOWMESTATS_ENDPOINT);
        httpMessage.setStartLineThirdToken(VERSION);
        httpMessage.addHeader(AUTH_HKEY, BEARER_HVAL + SP + authToken.toString());
        break;

      case SHOWME_RANKING:
        httpMessage.setStartLineFirstToken(GET);
        httpMessage.setStartLineSecondToken(SHOWMERANKING_ENDPOINT);
        httpMessage.setStartLineThirdToken(VERSION);
        httpMessage.addHeader(AUTH_HKEY, BEARER_HVAL + SP + authToken.toString());
        if (player != null)
          httpMessage.setBody(new Gson().toJson(player), ContentType.JSON);
        break;

      case SHARE:
        httpMessage.setStartLineFirstToken(POST);
        httpMessage.setStartLineSecondToken(SHARE_ENDPOINT);
        httpMessage.setStartLineThirdToken(VERSION);
        httpMessage.addHeader(AUTH_HKEY, BEARER_HVAL + SP + authToken.toString());
        break;
    }

    return httpMessage;
  }

  /**
  * The {@code Builder} class is used to construct instances of the {@link WordleHttpRequest} class
  * in a flexible and readable manner. It provides methods for setting various properties of the
  * request such as type, authentication token, credentials, word, and whether the request is fully
  * populated. Once all desired properties are set, the build method creates and returns a
  * fully-configured WordleHttpRequest instance.
  */
  public static class Builder {

    private Type type;
    private AuthToken authToken;
    private CredentialsDTO credentials;
    private WordDTO word;
    private PlayerDTO player;
    private boolean isFullyPopulated;

    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    public Builder withAuthToken(AuthToken authToken) {
      this.authToken = authToken;
      return this;
    }

    public Builder withCredentials(CredentialsDTO credentials) {
      this.credentials = credentials;
      return this;
    }

    public Builder withWord(WordDTO word) {
      this.word = word;
      return this;
    }

    public Builder withFullyPopulated(boolean isFullyPopulated) {
      this.isFullyPopulated = isFullyPopulated;
      return this;
    }

    public Builder withPlayer(PlayerDTO player) {
      this.player = player;
      return this;
    }

    public WordleHttpRequest build() {
      return new WordleHttpRequest(this);
    }
  }
}
