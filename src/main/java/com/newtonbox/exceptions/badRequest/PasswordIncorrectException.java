package com.newtonbox.exceptions.badRequest;

public class PasswordIncorrectException extends RuntimeException {

    public PasswordIncorrectException() {
        super("Password incorrect: The current password is incorrect.");
    }
}
