package io.github.yuricaprini.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.auth.AuthToken;
import io.github.yuricaprini.wordleprotocol.dtos.ClueDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadResponseException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseFullyPopulatedException;
import io.github.yuricaprini.wordleprotocol.exceptions.ResponseTooLargeException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.ErrorCode;
import io.github.yuricaprini.wordleprotocol.messages.WordleResponse.Type;

public class WordleResponseTest {
  private InputQueue inputQueue;
  private OutputQueue outputQueue;
  private WordleResponse.Factory responseFactory;

  @BeforeEach
  public void setUp() {
    int queueSize = WordleResponse.MAX_SIZE;
    inputQueue = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(queueSize);
    outputQueue = ProtocolFactoryProvider.newOutputQueueFactory().createOutputQueue();
    responseFactory = ProtocolFactoryProvider.newWordleResponseFactory();
    AuthToken.init("superSecretKey", 15 * 60 * 1000);
  }

  @Test
  void populateFromShouldThrowBadresponseExceptionIfStartLineMalformed1() throws IOException {
    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 malformed OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populateFromShouldThrowBadresponseExceptionIfStartLineMalformed2() throws IOException {
    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populateFromShouldThrowBadResponseExceptionIfHeaderLineMalformed() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populatFromShouldThrowBadResponseExceptionIfResponseTypeNotPresent() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: NOTEXISTS\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populatFromShouldThrowBadResponseExceptionIfResponseTypeWrong() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populatFromShouldThrowBadResponseExceptionIfContentTypeButNotContentLength()
      throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfContentLengthButNotContentType() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfContentTypeMalformed1() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application /json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfContentTypeMalformed2() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json;charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfContentTypeMalformed3() throws IOException {
    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charsetUTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfContentLengthValueIsNotANumber() throws IOException {
    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: notANumber\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);


    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfBodyCharsetNotSupported() throws IOException {
    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8NOTEXIST\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldReadCRInStartLineAndThenThrowingBadResponseException() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 \r200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfResponseBodyCharsetIsNotUTF8() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-16\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfResponseHasNotSupportedVersion() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.1 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void shouldThrowBadResponseExceptionIfResponseHasBodyWithWrongSyntax() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.replace('{', ' ').getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populateShouldThrowResponseFullyPopulatedExceptionIfResponseFullyPopulated()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);
    emptyResponse.populateFrom(inputQueue);

    ByteArrayInputStream bis1 = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel1 = Channels.newChannel(bis1);
    inputQueue.fillFrom(readableChannel1);

    assertThrows(ResponseFullyPopulatedException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
  }

  @Test
  void populateShouldThrowResponseTooLargeExceptionIfResponseExceedsMaxSize1() throws IOException {

    InputQueue inputQueue = ProtocolFactoryProvider.newInputQueueFactory()
        .createInputQueue(WordleResponse.MAX_SIZE + 1);

    byte[] startLine = new byte[WordleResponse.MAX_SIZE + 1];
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(ResponseTooLargeException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void populateShouldThrowResponseTooLargeExceptionIfResponseExceedsMaxSize2() throws IOException {

    InputQueue inputQueue = ProtocolFactoryProvider.newInputQueueFactory()
        .createInputQueue(WordleResponse.MAX_SIZE + 1);

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    String headerStrLine2 = "Content-Length: " + WordleResponse.MAX_SIZE + "\r\n";
    byte[] hLine2 = headerStrLine2.getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    int currentSize =
        startLine.length + hLine1.length + hLine2.length + hLine3.length + emptyLine.length;
    byte[] body = new byte[WordleResponse.MAX_SIZE + 1 - currentSize];

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(ResponseTooLargeException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void serializeToShouldReturnFalseIfResponseIsNotFullyPopulated() throws IOException {

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    assert (emptyResponse.serializeTo(outputQueue) == false);
  }

  // ******************* test for semantic validation starts here ***********************

  @Test
  void loginOKWithCodeNot200IsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 404 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
  }

  @Test
  void loginOKWithMeaningNotOKIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 NOTOK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
  }

  @Test
  void loginOKWithNotExactly3HeadersIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] hLine4 = ("Another-Header: Wrong\r\n").getBytes(cs);

    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(hLine4);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
  }

  @Test
  void loginOKWithContentTypeNotJsonIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String responseBody = new Gson().toJson(AuthToken.newIstance("username"), AuthToken.class);
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine1 = ("Content-Type: text/plain; charset=UTF-8\r\n").getBytes(cs);
    byte[] hLine2 = ("Content-Length: " + responseBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);

    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = responseBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);
    bos.write(body);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
  }

  @Test
  void loginOKWithoutBodyIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {
    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 200 OK\r\n").getBytes(cs);
    byte[] hLine3 = ("Response-Type: LOGIN_OK\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine3);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
  }

  @Test
  void loginOKShouldBeOk() throws IOException, ResponseTooLargeException, BadResponseException {

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    String username = "username";
    AuthToken authToken = AuthToken.newIstance(username);
    WordleResponse loginOKResponse = responseFactory.createLoginOK(authToken);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    WritableByteChannel writableChannel = Channels.newChannel(bos);
    loginOKResponse.serializeTo(outputQueue);
    outputQueue.drainTo(writableChannel);

    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      emptyResponse.populateFrom(inputQueue);
    }

    assert (emptyResponse.isFullyPopulated());
    assert (emptyResponse.getType() == Type.LOGIN_OK);
    assert (emptyResponse.getAuthToken().equals(authToken));
    assert (emptyResponse.getErrorCode() == null);
  }

  @Test
  void loginNOWithCodeNot401IsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 404 Unauthorized\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: LOGIN_NO\r\n").getBytes(cs);
    byte[] hLine2 = ("Error-Code: USERNAME_SHORT\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void loginNOWithMeaningNotUnauthorizedIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 401 NotUnauthorized\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: LOGIN_NO\r\n").getBytes(cs);
    byte[] hLine2 = ("Error-Code: USERNAME_SHORT\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void loginNOWithNotExactly2HeadersIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 401 Unauthorized\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: LOGIN_NO\r\n").getBytes(cs);
    byte[] hLine2 = ("Error-Code: USERNAME_SHORT\r\n").getBytes(cs);
    byte[] hLine3 = ("Another-Header: Wrong\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);


    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(hLine3);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void loginNOWithErrorCodeNotCorrectIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 401 Unauthorized\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: LOGIN_NO\r\n").getBytes(cs);
    byte[] hLine2 = ("Error-Code: ERROR_CODE_NOT_EXISTS\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }


  @Test
  void loginNOShouldBeOk() throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 401 Unauthorized\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: LOGIN_NO\r\n").getBytes(cs);
    byte[] hLine2 = ("Error-Code: USERNAME_SHORT\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      emptyResponse.populateFrom(inputQueue);
    }

    assert (emptyResponse.isFullyPopulated());
    assert (emptyResponse.getType() == Type.LOGIN_NO);
    assert (emptyResponse.getErrorCode() == ErrorCode.USERNAME_SHORT);
    assert (emptyResponse.getAuthToken() == null);
  }

  @Test
  void playwordleOKWithNo200CodeisWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 401 OK\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_OK\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleOKWitWithMeaningNotOKIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 200 NOTOK\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_OK\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleOKWithLessThan1HeaderIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 200 OK\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, null, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleOKWith2HeaderIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 200 OK\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_OK\r\n");
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + wrongH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleOKWitMoreThan3HeaderIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 200 OK\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_OK\r\n");
    ClueDTO[] clues = new ClueDTO[1];
    clues[0] = ClueDTO.newInstance("word", "+-+?");
    String body = new Gson().toJson(clues);
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes(StandardCharsets.ISO_8859_1).length + "\r\n";
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + ctypeH + clenH + wrongH, body));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleOKWitWithContentTypeNotJSONisWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 200 OK\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_OK\r\n");
    ClueDTO[] clues = new ClueDTO[1];
    clues[0] = ClueDTO.newInstance("word", "+-+?");
    String body = new Gson().toJson(clues);
    String ctypeH = "Content-Type: text/plain; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes(StandardCharsets.ISO_8859_1).length + "\r\n";

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + ctypeH + clenH, body));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playWordleOKWithoutBodyShouldBeOk()
      throws ResponseTooLargeException, BadResponseException, IOException {

    WordleResponse response = responseFactory.createPlayWordleOK(null);
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    fillFromTo(response, emptyResponse);

    assertEquals(Type.PLAYWORDLE_OK, emptyResponse.getType());
    assertNull(emptyResponse.getCluesDTO());
    assertNull(emptyResponse.getErrorCode());
    assertNull(emptyResponse.getAuthToken());
  }

  @Test
  void playWordleOKWithBodyShouldBeOk()
      throws ResponseTooLargeException, BadResponseException, IOException {

    ClueDTO[] clues = new ClueDTO[1];
    String word = "word";
    String wordColors = "+-+?";
    clues[0] = ClueDTO.newInstance(word, wordColors);
    WordleResponse response = responseFactory.createPlayWordleOK(clues);
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    fillFromTo(response, emptyResponse);

    assertEquals(Type.PLAYWORDLE_OK, emptyResponse.getType());
    assertEquals(word, emptyResponse.getCluesDTO()[0].getWord());
    assertEquals(wordColors, emptyResponse.getCluesDTO()[0].getWordColors());
    assertNull(emptyResponse.getErrorCode());
    assertNull(emptyResponse.getAuthToken());
  }

  @Test
  void playwordleNOWithNo401CodeisWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 200 Unauthorized\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_NO\r\n");
    String errCodeH = ("Error-Code: INVALID_AUTHTOKEN\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + errCodeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleNOWithMeaningNotUnauthorizedIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 401 OK\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_NO\r\n");
    String errCodeH = ("Error-Code: INVALID_AUTHTOKEN\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + errCodeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleNOWithLessThan2Header()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = ("HTTP/1.0 401 Unauthorized\r\n");
    String resTypeH = ("Response-Type: PLAYWORDLE_NO\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleNOWithMoreThan2Header()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = "HTTP/1.0 401 Unauthorized\r\n";
    String resTypeH = "Response-Type: PLAYWORDLE_NO\r\n";
    String errCodeH = ("Error-Code: INVALID_AUTHTOKEN\r\n");
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + wrongH + errCodeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleNOWithNoResponseTypeHeaderIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = "HTTP/1.0 401 Unauthorized\r\n";
    String errCodeH = ("Error-Code: INVALID_AUTHTOKEN\r\n");

    inputQueue.fillFrom(createMockChannel(startLine, errCodeH + errCodeH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playwordleNOWithNoErrorCodeHeaderIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    String startLine = "HTTP/1.0 401 Unauthorized\r\n";
    String resTypeH = "Response-Type: PLAYWORDLE_NO\r\n";
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startLine, resTypeH + wrongH, null));
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void playWordleNOShouldBeOk()
      throws ResponseTooLargeException, BadResponseException, IOException {

    WordleResponse response = responseFactory.createPlayWordleNO(ErrorCode.INVALID_AUTHTOKEN);
    WordleResponse emptyResponse = responseFactory.createEmptyResponse();

    fillFromTo(response, emptyResponse);

    assertEquals(Type.PLAYWORDLE_NO, emptyResponse.getType());
    assertEquals(ErrorCode.INVALID_AUTHTOKEN, emptyResponse.getErrorCode());
    assertNull(emptyResponse.getCluesDTO());
    assertNull(emptyResponse.getAuthToken());
  }

  @Test
  void badWithCodeNo400isWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 401 Bad Request\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: BAD\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void badWithNotExactly1HeadersIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 400 Bad Request\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: BAD\r\n").getBytes(cs);
    byte[] hLine2 = ("Dummy-Header: isDummy\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void badShouldBeOk() throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 400 Bad Request\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: BAD\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      emptyResponse.populateFrom(inputQueue);
    }

    assert (emptyResponse.isFullyPopulated());
    assert (emptyResponse.getType() == Type.BAD);
    assert (emptyResponse.getErrorCode() == null);
    assert (emptyResponse.getAuthToken() == null);
  }

  @Test
  void tooLargeWithCodeNo413isWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 412 Request Too Large\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: TOOLARGE\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void tooLargeWithNotExactly1HeadersIsWrong()
      throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 413 Request Too Large\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: TOOLARGE\r\n").getBytes(cs);
    byte[] hLine2 = ("Dummy-Header: isDummy\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(hLine2);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadResponseException.class, () -> {
      emptyResponse.populateFrom(inputQueue);
    });
    assertEmpty(emptyResponse);
  }

  @Test
  void tooLargeShouldBeOk() throws IOException, ResponseTooLargeException, BadResponseException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("HTTP/1.0 413 Request Too Large\r\n").getBytes(cs);
    byte[] hLine1 = ("Response-Type: TOOLARGE\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(hLine1);
    bos.write(emptyLine);

    WordleResponse emptyResponse = responseFactory.createEmptyResponse();
    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      emptyResponse.populateFrom(inputQueue);
    }

    assert (emptyResponse.isFullyPopulated());
    assert (emptyResponse.getType() == Type.TOOLARGE);
    assert (emptyResponse.getErrorCode() == null);
    assert (emptyResponse.getAuthToken() == null);
  }

  private static void assertEmpty(WordleResponse response) {
    assert (!response.isFullyPopulated());
    assert (response.getType() == null);
    assert (response.getErrorCode() == null);
    assert (response.getAuthToken() == null);
  }

  private static byte[][] splitByteArray(byte[] byteArray, int chunkSize) {

    int arrayLength = byteArray.length;
    int numOfChunks = (int) Math.ceil((double) arrayLength / chunkSize);
    byte[][] splitArrays = new byte[numOfChunks][];

    for (int i = 0; i < numOfChunks; i++) {
      int start = i * chunkSize;
      int length = Math.min(arrayLength - start, chunkSize);
      splitArrays[i] = Arrays.copyOfRange(byteArray, start, start + length);
    }

    return splitArrays;
  }

  private static ReadableByteChannel createMockChannel(String startline, String headers,
      String body) throws IOException {

    Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    Charset UTF_8 = StandardCharsets.UTF_8;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    if (startline != null)
      bos.write(startline.getBytes(ISO_8859_1));
    if (headers != null)
      bos.write(headers.getBytes(ISO_8859_1));

    bos.write("\r\n".getBytes(ISO_8859_1));
    if (body != null)
      bos.write(body.getBytes(UTF_8));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);

    return readableChannel;
  }

  private void fillFromTo(WordleResponse sourceResponse, WordleResponse targetResponse)
      throws ResponseTooLargeException, BadResponseException, IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    WritableByteChannel writableChannel = Channels.newChannel(bos);
    sourceResponse.serializeTo(outputQueue);
    outputQueue.drainTo(writableChannel);

    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      targetResponse.populateFrom(inputQueue);
    }
  }
}
