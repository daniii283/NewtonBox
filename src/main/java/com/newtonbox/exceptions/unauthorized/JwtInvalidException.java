package com.newtonbox.exceptions.unauthorized;

public class JwtInvalidException extends RuntimeException {
    public JwtInvalidException() {
        super("JWT token is invalid or expired.");
    }
}
