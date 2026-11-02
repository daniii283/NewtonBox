package com.newtonbox.exceptions.conflict;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String username) {
    super("User already exists with username: " + username);
  }
}
