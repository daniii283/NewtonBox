package com.newtonbox.exceptions.badRequest;

public class MissingFieldException extends RuntimeException {

  public MissingFieldException() {
    super("Missing field: A required field is missing or not provided.");
  }
}
