package com.newtonbox.exceptions.notFound;

public class UserNotFoundException extends BaseNotFoundException {
  public UserNotFoundException(Long id) {
    super("User not found with ID: " + id);
  }

  public UserNotFoundException(String username) {
    super("User not found with username: " + username);
  }
}
