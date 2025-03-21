package io.github.yuricaprini.wordleprotocol.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
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

  private Type type;
  private ErrorCode errorCode;
  private AuthToken authToken;

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

  /**
   * Private helper method to map {@code httpMessage} into a wordle response providing a semantic
   * validation (an {@code httpmessage} must have correctly set all and only the fields necessary 
   * to define a wordle response).
   * 
   * @param httpMessage the incoming HTTP message to be mapped.
   * @throws BadResponsetException if the HTTP message does not represents a wordle response. 
   */
  private void mapToWordleResponse(BasicHttpMessage httpMessage) throws BadResponseException {

    if (!httpMessage.getStartLineFirstToken().equals(BasicHttpMessage.VERSION))
      throw new BadResponseException();

    try {

      if (httpMessage.getHeaderValue("Response-Type") == null)
        throw new BadResponseException();

      switch (Type.valueOf(httpMessage.getHeaderValue("Response-Type"))) {

        case LOGIN_OK:

          if (!httpMessage.getStartLineSecondToken().equals("200")
              || !httpMessage.getStartLineThirdToken().equals("OK")
              || httpMessage.getHeaders().size() != 3
              || !httpMessage.getHeaderValue(BasicHttpMessage.CONTENT_TYPE_KEY)
                  .equals(BasicHttpMessage.CONTENT_TYPE_JSON)
              || httpMessage.getBody() == null)

            throw new BadResponseException();

          authToken = new Gson().fromJson(httpMessage.getBody(), AuthToken.class);
          type = Type.LOGIN_OK;

          break;

        case LOGIN_NO:

          if (!httpMessage.getStartLineSecondToken().equals("401")
              || !httpMessage.getStartLineThirdToken().equals("Unauthorized")
              || httpMessage.getHeaders().size() != 2
              || httpMessage.getHeaderValue("Error-Code") == null)
            throw new BadResponseException();

          errorCode = ErrorCode.valueOf(httpMessage.getHeaderValue("Error-Code"));
          type = Type.LOGIN_NO;

          break;

        case BAD:

          if (!httpMessage.getStartLineSecondToken().equals("400")
              || !httpMessage.getStartLineThirdToken().equals("Bad Request")
              || httpMessage.getHeaders().size() != 1)
            throw new BadResponseException();

          type = Type.BAD;

          break;

        case TOOLARGE:

          if (!httpMessage.getStartLineSecondToken().equals("413")
              || !httpMessage.getStartLineThirdToken().equals("Request Too Large")
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
        httpMessage.setStartLineFirstToken("HTTP/1.0");
        httpMessage.setStartLineSecondToken("200");
        httpMessage.setStartLineThirdToken("OK");
        httpMessage.addHeader("Response-Type", type.toString());
        httpMessage.setBody(new Gson().toJson(authToken), ContentType.JSON);
        break;

      case LOGIN_NO:
        httpMessage.setStartLineFirstToken("HTTP/1.0");
        httpMessage.setStartLineSecondToken("401");
        httpMessage.setStartLineThirdToken("Unauthorized");
        httpMessage.addHeader("Response-Type", type.toString());
        httpMessage.addHeader("Error-Code", errorCode.toString());
        break;

      case BAD:
        httpMessage.setStartLineFirstToken("HTTP/1.0");
        httpMessage.setStartLineSecondToken("400");
        httpMessage.setStartLineThirdToken("Bad Request");
        httpMessage.addHeader("Response-Type", type.toString());
        break;

      case TOOLARGE:
        httpMessage.setStartLineFirstToken("HTTP/1.0");
        httpMessage.setStartLineSecondToken("413");
        httpMessage.setStartLineThirdToken("Request Too Large");
        httpMessage.addHeader("Response-Type", type.toString());
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

    public Builder withFullyPopulated(boolean isFullyPopulated) {
      this.isFullyPopulated = isFullyPopulated;
      return this;
    }

    public WordleHttpResponse build() {
      return new WordleHttpResponse(this);
    }
  }
}
