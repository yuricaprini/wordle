package io.github.yuricaprini.unit;

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
import io.github.yuricaprini.wordleprotocol.ProtocolFactoryProvider;
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
import io.github.yuricaprini.wordleprotocol.exceptions.BadRequestException;
import io.github.yuricaprini.wordleprotocol.exceptions.RequestFullyPopulatedException;
import io.github.yuricaprini.wordleprotocol.exceptions.RequestTooLargeException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest;
import io.github.yuricaprini.wordleprotocol.messages.WordleRequest.Type;

public class WordleRequestTest {

  private InputQueue inputQueue;
  private OutputQueue outputQueue;
  private WordleRequest.Factory requestFactory;

  @BeforeEach
  public void setUp() {
    int queueSize = WordleRequest.MAX_SIZE;
    inputQueue = ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(queueSize);
    outputQueue = ProtocolFactoryProvider.newOutputQueueFactory().createOutputQueue();
    requestFactory = ProtocolFactoryProvider.newWordleRequestFactory();
  }

  @Test
  void populateFromShouldThrowBadRequestExceptionIfStartLineMalformed1() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login malformed HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void populateFromShouldThrowBadRequestExceptionIfStartLineMalformed2() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /loginHTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void populateFromShouldThrowBadRequestExceptionIfHeaderLineMalformed() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void populatFromShouldThrowBadRequestExceptionIfContentTypeButNotContentLength()
      throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfContentLengthButNotContentType() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfContentTypeMalformed1() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application /json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfContentTypeMalformed2() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json;charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfContentTypeMalformed3() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charsetUTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfContentLengthValueIsNotANumber() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = "POST /login HTTP/1.0\r\n".getBytes(cs);
    byte[] headerLine1 = "Content-Type: application/json; charset=UTF-8\r\n".getBytes(cs);
    byte[] headerLine2 = "Content-Length: 4notNumber5\r\n".getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = "{\"username\":\"username\",\"password\":\"password\"}".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfBodyCharsetNotSupported() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8NOEXISTS\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldReadCRInStartLineAndThenThrowingBadRequestException() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /log\rin HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfRequestBodyCharsetIsNotUTF8() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-16\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfRequestHasNotSupportedVersion() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.1\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void shouldThrowBadRequestExceptionIfRequestHasBodyWithWrongSyntax() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void populateShouldThrowRequestFullyPopulatedExceptionIfRequestFullyPopulated()
      throws IOException, RequestTooLargeException, BadRequestException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: application/json; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);
    emptyRequest.populateFrom(inputQueue);

    ByteArrayInputStream bis1 = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel1 = Channels.newChannel(bis1);
    inputQueue.fillFrom(readableChannel1);

    assertThrows(RequestFullyPopulatedException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assert (emptyRequest.isFullyPopulated() == true);
    assert (emptyRequest.getType() == Type.LOGIN);
    assert (emptyRequest.getCredentials().getUsername().equals("username"));
    assert (emptyRequest.getCredentials().getPassword().equals("password"));
    assert (emptyRequest.getAuthToken() == null);

  }

  @Test
  void populateShouldThrowRequestTooLargeExceptionIfRequestExceedsMaxSize1()
      throws IOException, BadRequestException {

    byte[] startLine = new byte[WordleRequest.MAX_SIZE];

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(RequestTooLargeException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void populateShouldThrowRequestTooLargeExceptionIfRequestExceedsMaxSize2()
      throws IOException, BadRequestException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = "POST /login HTTP/1.0\r\n".getBytes(cs);
    byte[] headerLine1 = "Content-Type: application/json; charset=UTF-8\r\n".getBytes(cs);
    String headerStrLine2 = "Content-Length: " + WordleRequest.MAX_SIZE + "\r\n";
    byte[] headerLine2 = headerStrLine2.getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    int currentSize = startLine.length + headerLine1.length + headerLine2.length + emptyLine.length;
    byte[] body = new byte[WordleRequest.MAX_SIZE - currentSize];

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(RequestTooLargeException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void serializeToShouldReturnFalseIfRequestIsNotFullyPopulated() throws IOException {

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    assert (emptyRequest.serializeTo(outputQueue) == false);
  }
  // ******************* test for semantic validation starts here ***********************



  @Test
  void loginWithContentTypeNotJsonIsWrong() throws IOException {

    Charset cs = StandardCharsets.ISO_8859_1;
    String requestBody = "{\"username\":\"username\",\"password\":\"password\"}";
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] headerLine1 = ("Content-Type: text/plain; charset=UTF-8\r\n").getBytes(cs);
    byte[] headerLine2 = ("Content-Length: " + requestBody.getBytes().length + "\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    byte[] body = requestBody.getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(headerLine1);
    bos.write(headerLine2);
    bos.write(emptyLine);
    bos.write(body);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithMoreThanTwoHeaderIsWrong()
      throws IOException, RequestTooLargeException, BadRequestException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = "POST /login HTTP/1.0\r\n".getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(emptyLine);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithoutBodyIsWrong() throws IOException, RequestTooLargeException, BadRequestException {

    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = ("POST /login HTTP/1.0\r\n").getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(startLine);
    bos.write(emptyLine);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ReadableByteChannel readableChannel = Channels.newChannel(bis);
    inputQueue.fillFrom(readableChannel);

    assertThrows(BadRequestException.class, () -> {
      emptyRequest.populateFrom(inputQueue);
    });
    assertEmpty(emptyRequest);
  }

  @Test
  void loginShouldBeOk() throws IOException, RequestTooLargeException, BadRequestException {

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    String username = "username";
    String password = "password";
    WordleRequest loginRequest =
        requestFactory.createLoginRequest(CredentialsDTO.newInstance(username, password));

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    WritableByteChannel writableChannel = Channels.newChannel(bos);
    loginRequest.serializeTo(outputQueue);
    outputQueue.drainTo(writableChannel);

    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      emptyRequest.populateFrom(inputQueue);
    }

    assert (emptyRequest.isFullyPopulated());
    assert (emptyRequest.getType() == WordleRequest.Type.LOGIN);
    assert (emptyRequest.getCredentials().getUsername().equals(username));
    assert (emptyRequest.getCredentials().getPassword().equals(password));
    assert (emptyRequest.getAuthToken() == null);
  }

  // @Test
  // void flowLogoutShouldBeOk() throws IOException, RequestTooLargeException, BadRequestException {

  //   WordleRequest emptyRequest = requestFactory.createEmptyRequest();
  //   AuthToken token = AuthToken.createFromString("faketoken");
  //   WordleRequest logoutRequest = requestFactory.createLogoutRequest(token);

  //   ByteArrayOutputStream bos = new ByteArrayOutputStream();
  //   WritableByteChannel writableChannel = Channels.newChannel(bos);
  //   logoutRequest.serializeTo(outputQueue);
  //   outputQueue.drainTo(writableChannel);

  //   byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
  //   for (byte[] splitArray : splitArrays) {
  //     ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
  //     ReadableByteChannel readableChannel = Channels.newChannel(bis);
  //     inputQueue.fillFrom(readableChannel);
  //     emptyRequest.populateFrom(inputQueue);
  //   }

  //   assert (emptyRequest.isFullyPopulated());
  //   assert (emptyRequest.getType() == WordleRequest.Type.LOGOUT);
  //   assertEquals(emptyRequest.getAuthToken(), token);
  // }

  private static void assertEmpty(WordleRequest request) {
    assert (!request.isFullyPopulated());
    assert (request.getType() == null);
    assert (request.getCredentials() == null);
    assert (request.getWord() == null);
    assert (request.getAuthToken() == null);
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
}
