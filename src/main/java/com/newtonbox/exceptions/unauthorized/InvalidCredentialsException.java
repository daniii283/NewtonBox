package com.newtonbox.exceptions.unauthorized;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException() {
    super("Invalid username or password.");
  }
}
