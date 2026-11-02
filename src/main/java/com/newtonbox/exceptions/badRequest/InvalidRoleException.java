package com.newtonbox.exceptions.badRequest;

public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException() {
        super("Invalid role: The specified role is not recognized or permitted.");
    }
}
