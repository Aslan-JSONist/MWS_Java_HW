package com.example.todolist.exception;

public class ExternalServiceUnavailableException extends RuntimeException {

  public ExternalServiceUnavailableException(String message) {
    super(message);
  }
}
