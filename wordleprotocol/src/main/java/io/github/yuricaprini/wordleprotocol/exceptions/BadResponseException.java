package io.github.yuricaprini.wordleprotocol.exceptions;

public class BadResponseException extends Exception {

  public BadResponseException() {}

  public BadResponseException(Throwable e) {
    super(e);
  }
}
