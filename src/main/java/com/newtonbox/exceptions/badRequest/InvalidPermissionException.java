package com.newtonbox.exceptions.badRequest;

public class InvalidPermissionException extends RuntimeException {

    public InvalidPermissionException() {
        super("Invalid permission: You do not have the required permission to perform this action.");
    }
}
