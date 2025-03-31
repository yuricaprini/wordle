package io.github.yuricaprini.wordleprotocol.exceptions;

public class ResponseTooLargeException extends Exception {

  public ResponseTooLargeException() {}

  public ResponseTooLargeException(Throwable e) {
    super(e);
  }
}
