package dev.sagar.critique.exceptions;

public class BaseException extends RuntimeException {

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public BaseException(Throwable cause) {
    super(cause);
  }

  public BaseException() {}
}
