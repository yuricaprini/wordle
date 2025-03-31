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
import io.github.yuricaprini.wordleprotocol.dtos.CredentialsDTO;
import io.github.yuricaprini.wordleprotocol.dtos.WordDTO;
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
    assert (emptyRequest.getCredentialsDTO().getUsername().equals("username"));
    assert (emptyRequest.getCredentialsDTO().getPassword().equals("password"));
    assert (emptyRequest.getAuthToken() == null);

  }

  @Test
  void populateShouldThrowRequestTooLargeExceptionIfRequestExceedsMaxSize1()
      throws IOException, BadRequestException {

    InputQueue inputQueue =
        ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(WordleRequest.MAX_SIZE + 1);

    byte[] startLine = new byte[WordleRequest.MAX_SIZE + 1];

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

    InputQueue inputQueue =
        ProtocolFactoryProvider.newInputQueueFactory().createInputQueue(WordleRequest.MAX_SIZE + 1);
    Charset cs = StandardCharsets.ISO_8859_1;
    byte[] startLine = "POST /login HTTP/1.0\r\n".getBytes(cs);
    byte[] headerLine1 = "Content-Type: application/json; charset=UTF-8\r\n".getBytes(cs);
    String headerStrLine2 = "Content-Length: " + WordleRequest.MAX_SIZE + "\r\n";
    byte[] headerLine2 = headerStrLine2.getBytes(cs);
    byte[] emptyLine = "\r\n".getBytes(cs);
    int currentSize = startLine.length + headerLine1.length + headerLine2.length + emptyLine.length;
    byte[] body = new byte[WordleRequest.MAX_SIZE + 1 - currentSize];

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
  void loginWithLessThan2HeaderIsWrong()
      throws IOException, RequestTooLargeException, BadRequestException {

    String startline = "POST /login HTTP/1.0\r\n";
    String body = new Gson().toJson(CredentialsDTO.newInstance("username", "password"));
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";

    inputQueue.fillFrom(createMockChannel(startline, clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithMoreThan2HeaderIsWrong()
      throws IOException, RequestTooLargeException, BadRequestException {

    String startline = "POST /login HTTP/1.0\r\n";
    String body = new Gson().toJson(CredentialsDTO.newInstance("username", "password"));
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes(StandardCharsets.ISO_8859_1).length + "\r\n";
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startline, wrongH + ctypeH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithoutContentTypeHeader() throws IOException {

    String startline = "POST /login HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";

    inputQueue.fillFrom(createMockChannel(startline, clenH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithoutContentLenHeader() throws IOException {

    String startline = "POST /login HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";

    inputQueue.fillFrom(createMockChannel(startline, ctypeH + ctypeH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithoutBodyIsWrong() throws IOException, RequestTooLargeException, BadRequestException {

    String startline = "POST /login HTTP/1.0\r\n";

    inputQueue.fillFrom(createMockChannel(startline, null, null));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void loginWithContentTypeNotJsonIsWrong() throws IOException {

    String startline = "POST /login HTTP/1.0\r\n";
    String body = new Gson().toJson(CredentialsDTO.newInstance("username", "password"));
    String ctypeH = "Content-Type: text/plain; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";
    String authH = "Authorization: Bearer xyz\r\n";

    inputQueue.fillFrom(createMockChannel(startline, authH + ctypeH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void loginShouldBeOk() throws IOException, RequestTooLargeException, BadRequestException {

    String username = "username";
    String password = "password";
    CredentialsDTO credentialsDTO = CredentialsDTO.newInstance(username, password);
    WordleRequest request = requestFactory.createLoginRequest(credentialsDTO);
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    fillFromTo(request, emptyRequest);

    assertEquals(Type.LOGIN, emptyRequest.getType());
    assertNull(emptyRequest.getAuthToken());
    assertEquals(username, emptyRequest.getCredentialsDTO().getUsername());
    assertEquals(password, emptyRequest.getCredentialsDTO().getPassword());
    assertNull(emptyRequest.getWordDTO());
  }

  @Test
  void playWordleWithoutHeadersIsWrong() throws IOException {
    inputQueue.fillFrom(createMockChannel("POST /playwordle HTTP/1.0\r\n", null, null));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void playWordleWithoutAuthorizationHeaderIsWrong() throws IOException {
    inputQueue.fillFrom(createMockChannel("POST /playwordle HTTP/1.0\r\n", "Is: wrong\r\n", null));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void playWordleShouldBeOK() throws IOException, RequestTooLargeException, BadRequestException {

    AuthToken authToken = AuthToken.wrap("token");
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    WordleRequest request = requestFactory.createPlayWordleRequest(authToken);

    fillFromTo(request, emptyRequest);

    assertEquals(Type.PLAY_WORDLE, emptyRequest.getType());
    assertEquals(authToken, emptyRequest.getAuthToken());
    assertNull(emptyRequest.getCredentialsDTO());
    assertNull(emptyRequest.getWordDTO());
  }

  @Test
  void sendWordWithLessThan3HeadersIsWrong() throws IOException {
    String startline = "POST /sendword HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";

    inputQueue.fillFrom(createMockChannel(startline, ctypeH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordWithMoreThan3HeadersIsWrong() throws IOException {
    String startline = "POST /sendword HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";
    String authH = "Authorization: Bearer xyz\r\n";
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startline, authH + wrongH + ctypeH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordWithoutAuthorizationHeader() throws IOException {

    String startline = "POST /sendword HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";
    String wrongH = "Wrong:Header\r\n";

    inputQueue.fillFrom(createMockChannel(startline, wrongH + ctypeH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordWithoutContentTypeHeader() throws IOException {

    String startline = "POST /sendword HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";
    String authH = "Authorization: Bearer xyz\r\n";

    inputQueue.fillFrom(createMockChannel(startline, authH + authH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordWithoutContentLenHeader() throws IOException {

    String startline = "POST /sendword HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String ctypeH = "Content-Type: application/json; charset=UTF-8\r\n";
    String authH = "Authorization: Bearer xyz\r\n";

    inputQueue.fillFrom(createMockChannel(startline, authH + authH + ctypeH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordWithContentTypeNotJsonIsWrong() throws IOException {

    String startline = "POST /sendword HTTP/1.0\r\n";
    String body = new Gson().toJson(WordDTO.newInstance("word"));
    String ctypeH = "Content-Type: text/plain; charset=UTF-8\r\n";
    String clenH = "Content-Length: " + body.getBytes().length + "\r\n";
    String authH = "Authorization: Bearer xyz\r\n";

    inputQueue.fillFrom(createMockChannel(startline, authH + ctypeH + clenH, body));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordWithoutBody() throws IOException {

    String startline = "POST /sendword HTTP/1.0\r\n";
    String authH = "Authorization: Bearer xyz\r\n";

    inputQueue.fillFrom(createMockChannel(startline, authH, null));
    WordleRequest emptyRequest = requestFactory.createEmptyRequest();

    assertThrows(BadRequestException.class, () -> emptyRequest.populateFrom(inputQueue));
    assertEmpty(emptyRequest);
  }

  @Test
  void sendWordShouldBeOK() throws IOException, RequestTooLargeException, BadRequestException {

    AuthToken authToken = AuthToken.wrap("token");
    WordDTO wordDTO = WordDTO.newInstance("word");
    WordleRequest request = requestFactory.createSendWordRequest(authToken, wordDTO);

    WordleRequest emptyRequest = requestFactory.createEmptyRequest();
    fillFromTo(request, emptyRequest);

    assertEquals(Type.SEND_WORD, emptyRequest.getType());
    assertEquals(authToken, emptyRequest.getAuthToken());
    assertEquals(wordDTO.getWord(), emptyRequest.getWordDTO().getWord());
    assertNull(emptyRequest.getCredentialsDTO());
  }

  private static void assertEmpty(WordleRequest request) {
    assert (!request.isFullyPopulated());
    assert (request.getType() == null);
    assert (request.getCredentialsDTO() == null);
    assert (request.getWordDTO() == null);
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

  private void fillFromTo(WordleRequest sourceRequest, WordleRequest targetRequest)
      throws IOException, RequestTooLargeException, BadRequestException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    WritableByteChannel writableChannel = Channels.newChannel(bos);
    sourceRequest.serializeTo(outputQueue);
    outputQueue.drainTo(writableChannel);

    byte[][] splitArrays = splitByteArray(bos.toByteArray(), 1);
    for (byte[] splitArray : splitArrays) {
      ByteArrayInputStream bis = new ByteArrayInputStream(splitArray);
      ReadableByteChannel readableChannel = Channels.newChannel(bis);
      inputQueue.fillFrom(readableChannel);
      targetRequest.populateFrom(inputQueue);
    }
  }
}
