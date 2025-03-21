package io.github.yuricaprini.wordleprotocol.exceptions;

public class BadRequestException extends Exception {

  public BadRequestException() {

  }

  public BadRequestException(Throwable e) {
    super(e);
  }
}
