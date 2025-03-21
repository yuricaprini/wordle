package io.github.yuricaprini.wordleprotocol.implementations;

import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
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

  private Type type;
  private AuthToken authToken;
  private CredentialsDTO credentials;
  private WordDTO word;

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
  public CredentialsDTO getCredentials() {
    return this.credentials;
  }

  @Override
  public WordDTO getWord() {
    return this.word;
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

    if (!httpMessage.getStartLineThirdToken().equals(BasicHttpMessage.VERSION))
      throw new BadRequestException();

    try {

      switch (httpMessage.getStartLineFirstToken() + BasicHttpMessage.SP
          + httpMessage.getStartLineSecondToken()) {

        case "POST /login":

          if (httpMessage.getHeaders().size() != 2
              || !httpMessage.getHeaderValue(BasicHttpMessage.CONTENT_TYPE_KEY)
                  .equals(BasicHttpMessage.CONTENT_TYPE_JSON)
              || httpMessage.getBody() == null)
            throw new BadRequestException();

          credentials = new Gson().fromJson(httpMessage.getBody(), CredentialsDTO.class);
          type = Type.LOGIN;

          break;

        // case "POST /logout":

        //   if (httpMessage.getBody() != null)
        //     throw new BadRequestException();

        //   type = Type.LOGOUT;

        //   break;

        // case "POST /playwordle":

        //   if (httpMessage.getBody() != null)
        //     throw new BadRequestException();

        //   type = Type.PLAY_WORDLE;

        //   break;

        // case "GET /sendmestats":

        //   if (httpMessage.getBody() != null)
        //     throw new BadRequestException();

        //   type = Type.SENDME_STATS;

        //   break;

        // case "POST /sendword":

        //   if (httpMessage.getBody() == null)
        //     throw new BadRequestException();

        //   if (!httpMessage.getHeaderValue(BasicHttpMessage.CONTENT_TYPE_KEY)
        //       .equals(BasicHttpMessage.CONTENT_TYPE_JSON))
        //     throw new BadRequestException();

        //   type = Type.SEND_WORD;
        //   word = new Gson().fromJson(httpMessage.getBody(), WordDTO.class);

        //   break;

        // case "GET /share":

        //   if (httpMessage.getBody() != null)
        //     throw new BadRequestException();

        //   type = Type.SHARE;

        //   break;

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
        httpMessage.setStartLineFirstToken("POST");
        httpMessage.setStartLineSecondToken("/login");
        httpMessage.setStartLineThirdToken("HTTP/1.0");
        httpMessage.setBody(new Gson().toJson(credentials), ContentType.JSON);
        break;

      // case LOGOUT:
      //   httpMessage.setStartLineFirstToken("POST");
      //   httpMessage.setStartLineSecondToken("/logout");
      //   httpMessage.setStartLineThirdToken("HTTP/1.0");
      //   //TODO set auth header
      //   break;

      // case PLAY_WORDLE:
      //   httpMessage.setStartLineFirstToken("POST");
      //   httpMessage.setStartLineSecondToken("/playwordle");
      //   httpMessage.setStartLineThirdToken("HTTP/1.0");
      //   break;

      // case SENDME_STATS:
      //   httpMessage.setStartLineFirstToken("GET");
      //   httpMessage.setStartLineSecondToken("/sendmestats");
      //   httpMessage.setStartLineThirdToken("HTTP/1.0");
      //   break;

      // case SEND_WORD:
      //   httpMessage.setStartLineFirstToken("POST");
      //   httpMessage.setStartLineSecondToken("/sendword");
      //   httpMessage.setStartLineThirdToken("HTTP/1.0");
      //   httpMessage.setBody(new Gson().toJson(word), ContentType.JSON);
      //   break;

      // case SHARE:
      //   httpMessage.setStartLineFirstToken("POST");
      //   httpMessage.setStartLineSecondToken("/share");
      //   httpMessage.setStartLineThirdToken("HTTP/1.0");
      //   break;

      default:
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

    public WordleHttpRequest build() {
      return new WordleHttpRequest(this);
    }
  }
}
