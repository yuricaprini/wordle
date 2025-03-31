package io.github.yuricaprini.wordleprotocol.implementations;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import io.github.yuricaprini.wordleprotocol.exceptions.MalformedHttpMessageException;
import io.github.yuricaprini.wordleprotocol.exceptions.TooLongHttpMessageException;
import io.github.yuricaprini.wordleprotocol.ioutils.InputQueue;
import io.github.yuricaprini.wordleprotocol.ioutils.OutputQueue;

/**
 * The {@code BasicHttpCodec} class provides functionality for encoding and decoding HTTP messages.
 * It can parse HTTP messages from an input queue and encode HTTP messages to an output queue.
 */
class BasicHttpCodec {

  private final int DEFAULT_INIT_SEC_SIZE = 128;
  private final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

  private int maxHttpMsgSize;
  private int httpMsgSize;
  private DecodeState decodeState;
  private DecodeLineState decodeLineState;
  private ByteArrayOutputStream section;
  private int bodyLength;
  private String bodyCharset;
  private BasicHttpMessage basicHttpMessage;

  /**
   * Constructs a new {@code BasicHttpCodec} able to decode and encode HTTP messages of maximum
   * length {@code maxHttpMsgSize}.
   *
   * @param maxHttpMsgSize the maximum size allowed for HTTP messages to encode and decode.
   */
  BasicHttpCodec(int maxHttpMsgSize) {
    this.maxHttpMsgSize = maxHttpMsgSize;
    this.httpMsgSize = 0;
    this.decodeState = DecodeState.STARTLINE;
    this.decodeLineState = DecodeLineState.READ_CR;
    this.section = new ByteArrayOutputStream(DEFAULT_INIT_SEC_SIZE);
    this.bodyLength = 0;
    this.bodyCharset = null;
    this.basicHttpMessage = new BasicHttpMessage();
  }

  /**
   * Decodes an HTTP message from the input queue, returning a {@code BasicHttpMessage}.
   *
   * @param inputQueue the input queue containing the HTTP message in bytes.
   * @return a {@code BasicHttpMessage} if the HTTP message is successfully decoded, or 
   * {@code null} if the message is not fully available yet.
   * @throws MalformedHttpMessageException if the HTTP message is malformed.
   * @throws TooLongHttpMessageException if the HTTP message exceeds the maximum allowed size.
   */
  BasicHttpMessage decode(InputQueue inputQueue)
      throws MalformedHttpMessageException, TooLongHttpMessageException {


    String startLine, headerLine, body = null;
    switch (decodeState) {

      case STARTLINE:
        if ((startLine = decodeLine(inputQueue)) == null)
          return null;

        String[] tokens = startLine.split(BasicHttpMessage.SP, 3);

        if (tokens.length < 3)
          throw new MalformedHttpMessageException();

        basicHttpMessage.setStartLineFirstToken(tokens[0]);
        basicHttpMessage.setStartLineSecondToken(tokens[1]);
        basicHttpMessage.setStartLineThirdToken(tokens[2]);

        decodeState = DecodeState.HEADER;

      case HEADER:

        while (decodeState == DecodeState.HEADER) {

          if ((headerLine = decodeLine(inputQueue)) == null)
            return null;

          if (headerLine.isEmpty())
            decodeState = DecodeState.BODY;
          else {
            tokens = headerLine.split(BasicHttpMessage.COLON, 2);

            if (tokens.length < 2)
              throw new MalformedHttpMessageException();

            basicHttpMessage.addHeader(tokens[0], tokens[1].trim());
          }
        }

      case BODY:

        if (bodyLength == 0) {
          String contentLength = basicHttpMessage.getHeaderValue(BasicHttpMessage.CONTENTLEN_HKEY);
          String contentType = basicHttpMessage.getHeaderValue(BasicHttpMessage.CONTENTTYPE_HKEY);

          if (contentLength == null && contentType == null)
            return basicHttpMessage;

          if (contentLength == null || contentType == null)
            throw new MalformedHttpMessageException();

          try {
            bodyLength = Integer.parseUnsignedInt(contentLength);
          } catch (NumberFormatException e) {
            throw new MalformedHttpMessageException(e);
          }

          tokens = contentType.split(BasicHttpMessage.SP, 2);

          if (tokens.length < 2)
            bodyCharset = ISO_8859_1.name();
          else {
            tokens = tokens[1].split(BasicHttpMessage.EQ, 2);
            if (tokens.length < 2)
              throw new MalformedHttpMessageException();
            bodyCharset = tokens[1];
          }

        }

        if ((body = decodeBody(inputQueue)) == null)
          return null;

        basicHttpMessage.setBody(body);
        decodeState = DecodeState.STARTLINE;
    }

    return basicHttpMessage;

  }

  /**
   * Encodes a {@code BasicHttpMessage} object to an output queue.
   *
   * @param httpMessage the {@code BasicHttpMessage} to encode.
   * @param outputQueue the output queue where the encoded message will be written.
   */
  void encode(BasicHttpMessage httpMessage, OutputQueue outputQueue) {

    outputQueue.enqueue(httpMessage.getStartLineFirstToken().getBytes(ISO_8859_1));
    outputQueue.enqueue(BasicHttpMessage.SP.getBytes(ISO_8859_1));
    outputQueue.enqueue(httpMessage.getStartLineSecondToken().getBytes(ISO_8859_1));
    outputQueue.enqueue(BasicHttpMessage.SP.getBytes(ISO_8859_1));
    outputQueue.enqueue(httpMessage.getStartLineThirdToken().getBytes(ISO_8859_1));
    outputQueue.enqueue(BasicHttpMessage.CRLF.getBytes(ISO_8859_1));

    for (Entry<String, String> header : httpMessage.getHeaders()) {
      outputQueue.enqueue(header.getKey().getBytes(ISO_8859_1));
      outputQueue.enqueue(BasicHttpMessage.COLON.getBytes(ISO_8859_1));
      outputQueue.enqueue(header.getValue().getBytes(ISO_8859_1));
      outputQueue.enqueue(BasicHttpMessage.CRLF.getBytes(ISO_8859_1));
    }
    outputQueue.enqueue(BasicHttpMessage.CRLF.getBytes(ISO_8859_1));

    if (httpMessage.getCachedEncodedBody() != null)
      outputQueue.enqueue(httpMessage.getCachedEncodedBody());
  }

  /**
   * Private helper method to decode a line from the input queue.
   *
   * @param inputQueue the input queue containing the HTTP message line in bytes.
   * @return the decoded line if successfully decoded, or {@code null} if not complete yet.
   * @throws TooLongHttpMessageException if the HTTP message exceeds the maximum allowed size.
   */
  private String decodeLine(InputQueue inputQueue) throws TooLongHttpMessageException {

    byte CRbyte = BasicHttpMessage.CR.getBytes(ISO_8859_1)[0];
    byte LFbyte = BasicHttpMessage.LF.getBytes(ISO_8859_1)[0];
    boolean CRLFFound = false;
    String decodedLine = null;

    while (!inputQueue.isEmpty() && !CRLFFound) {
      byte dequeuedByte = inputQueue.dequeue();

      httpMsgSize++;

      if (httpMsgSize > maxHttpMsgSize) {
        throw new TooLongHttpMessageException();
      }
      switch (decodeLineState) {

        case READ_CR:

          if (dequeuedByte == CRbyte)
            decodeLineState = DecodeLineState.READ_LF;
          else
            section.write(dequeuedByte);

          break;

        case READ_LF:

          if (dequeuedByte == LFbyte) {
            try {
              decodedLine = section.toString(ISO_8859_1.name());
              section.reset();
            } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
            } //never occurs
            CRLFFound = true;

          } else {
            section.write(CRbyte);
            section.write(dequeuedByte);
          }

          decodeLineState = DecodeLineState.READ_CR;
          break;

        default:
          break;
      }
    }

    return decodedLine;
  }

  /**
   * Private helper method to decode the message body from the input queue.
   *
   * @param inputQueue the input queue containing the HTTP message body in bytes.
   * @return the decoded body if successfully decoded, or {@code null} if not complete yet.
   * @throws MalformedHttpMessageException if the HTTP message body is malformed.
   * @throws TooLongHttpMessageException if the HTTP message exceeds the maximum allowed size.
   */
  private String decodeBody(InputQueue inputQueue)
      throws MalformedHttpMessageException, TooLongHttpMessageException {

    while (bodyLength > 0) {
      if (inputQueue.isEmpty())
        return null;

      section.write(inputQueue.dequeue());
      bodyLength--;

      httpMsgSize++;
      if (httpMsgSize > maxHttpMsgSize)
        throw new TooLongHttpMessageException();
    }

    try {

      String body = section.toString(bodyCharset);
      section.reset();
      return body;

    } catch (UnsupportedEncodingException e) {
      throw new MalformedHttpMessageException(e);
    }
  }

  /**
   * Enum defining the possible states during the line decoding.
   */
  private enum DecodeLineState {
    READ_CR, READ_LF
  }

  /**
   * Enum defining the possible states during HTTP message decoding.
   */
  private enum DecodeState {
    STARTLINE, HEADER, BODY
  }
}
