package com.newtonbox.exceptions.badRequest;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("Password mismatch: The new passwords do not match.");
    }
}
