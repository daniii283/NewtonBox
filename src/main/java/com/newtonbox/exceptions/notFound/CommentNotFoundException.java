package com.newtonbox.exceptions.notFound;

public class CommentNotFoundException extends BaseNotFoundException {
  public CommentNotFoundException(Long id) {
    super("Comment not found with ID: " + id);
  }
}
