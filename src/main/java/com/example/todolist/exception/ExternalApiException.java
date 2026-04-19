package com.example.todolist.exception;

/**
 * Thrown when the external tasks API returns an unexpected error (5xx or similar).
 */
public class ExternalApiException extends RuntimeException {

  private final int statusCode;

  public ExternalApiException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public ExternalApiException(int statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
