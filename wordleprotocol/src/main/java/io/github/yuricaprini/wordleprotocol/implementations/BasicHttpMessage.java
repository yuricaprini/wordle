package io.github.yuricaprini.wordleprotocol.implementations;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * The {@code BasicHttpMessage} class represents an HTTP message following the structure defined by
 * the RFC 9112 standard (https://datatracker.ietf.org/doc/html/rfc9112#name-message).
 * It consists of a start line, zero or more header lines, and an optional message body:
 * 
 * HTTP-message = start-line CRLF 
 *                *(header-line CRLF ) 
 *                CRLF 
 *                [ message-body ]
 *
 * The start-line can be either a:
 *
 * - request-line: method SP path SP HTTP-version
 * - status-line:  HTTP-version SP status-code SP [reason-phrase]
 * 
 * The header-line has the following format:
 *    header-name ":" OWS header-value OWS
 * 
 * This class provides methods for setting and getting various components of an HTTP message,
 * including start line tokens, headers, and the message body.
 *
 * It also supports specifying and encoding the message body with specific content types such as 
 * JSON or plain text.
 */
class BasicHttpMessage {

  static final String CR = "\r";
  static final String LF = "\n";
  static final String SP = " ";
  static final String COLON = ":";
  static final String EQ = "=";
  static final String CRLF = "\r\n";
  static final String VERSION = "HTTP/1.0";
  static final String CONTENT_TYPE_KEY = "Content-Type";
  static final String CONTENT_LEN_KEY = "Content-Length";
  static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
  static final String CONTENT_TYPE_TEXT = "text/plain";

  private String[] startLine;
  private Map<String, String> headers;
  private String body;
  private byte[] cachedEncodedBody;

  /**
   * Constructs a new empty {@code BasicHttpMessage}.
   */
  public BasicHttpMessage() {
    this.startLine = new String[3];
    this.headers = new HashMap<String, String>();
  }

  /**
   * Sets the first token of the start-line (method if request-line, HTTP-version if status-line).
   *
   * @param firstToken the first token of the start-line.
   */
  public void setStartLineFirstToken(String firstToken) {
    this.startLine[0] = firstToken;
  }

  /**
   * Gets the first token of the start line (method if request-line, HTTP-version if status-line).
   *
   * @return the first token of the start-line.
   */
  public String getStartLineFirstToken() {
    return this.startLine[0];
  }

  /**
   * Sets the second token of the start-line (path if request-line, status-code if status-line).
   *
   * @param secondToken the second token of the start-line.
   */
  public void setStartLineSecondToken(String secondToken) {
    this.startLine[1] = secondToken;
  }

  /**
   * Gets the second token of the start-line (path if request-line, status-code if status-line).
   *
   * @return the second token of the start-line.
   */
  public String getStartLineSecondToken() {
    return this.startLine[1];
  }

  /**
   * Sets the third token of the start line (HTTP-version if request-line, reason-phrase if 
   * status-line).
   *
   * @param thirdToken the third token of the start line.
   */
  public void setStartLineThirdToken(String thirdToken) {
    this.startLine[2] = thirdToken;
  }

  /**
   * Gets the third token of the start line (HTTP-version if request-line, reason-phrase if 
   * status-line).
   *
   * @return the third token of the start line (HTTP-version if request-line, reason-phrase if 
   * status-line).
   */
  public String getStartLineThirdToken() {
    return this.startLine[2];
  }

  /**
   * Adds an HTTP header with the specified name and value to the message.
   *
   * @param name  the name of the header.
   * @param value the value of the header.
   */
  public void addHeader(String name, String value) {
    this.headers.put(name, value);
  }

  /**
   * Gets the value of the specified header.
   *
   * @param name the name of the header.
   * @return the value of the header, or null if the header is not present.
   */
  public String getHeaderValue(String name) {
    return this.headers.get(name);
  }

  /**
   * Returns an unmodifiable set of key-value pairs representing the headers contained in this 
   * message.
   *
   * @return an unmodifiable set of key-value pairs representing the headers contained in this 
   * message.
   */
  public Set<Entry<String, String>> getHeaders() {
    return Collections.unmodifiableSet(headers.entrySet());
  }

  /**
   * Sets the message body.
   *
   * @param body the message body.
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * Sets the message body along with the corrisponding content-length and content-type headers.
   * The encoded body created to measure the content-length in bytes, is cached for perfomance 
   * reasons.
   *
   * @param body        the message body.
   * @param contentType the content type of the body (e.g., JSON or plain text).
   */
  public void setBody(String body, ContentType contentType) {
    this.body = body;
    switch (contentType) {

      case JSON:
        this.cachedEncodedBody = body.getBytes(StandardCharsets.UTF_8);
        this.addHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_JSON);
        break;

      default:
        break;
    }
    this.addHeader(CONTENT_LEN_KEY, String.valueOf(this.cachedEncodedBody.length));
  }

  /**
   * Gets the message body.
   *
   * @return the message body.
   */
  public String getBody() {
    return this.body;
  }

  /**
   * Gets the cached encoded body as a byte array.
   *
   * @return the cached encoded body.
   */
  public byte[] getCachedEncodedBody() {
    return this.cachedEncodedBody;
  }

  /**
   * Enumeration of content types for setting the message body.
   */
  public enum ContentType {
    JSON
  }
}
