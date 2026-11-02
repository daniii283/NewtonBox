package com.newtonbox.exceptions.notFound;

public class ResultNotFoundException extends BaseNotFoundException {
    public ResultNotFoundException(Long id) {
        super("Result not found with ID: " + id);
    }
}
