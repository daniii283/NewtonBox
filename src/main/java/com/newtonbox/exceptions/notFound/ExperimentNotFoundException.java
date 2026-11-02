package com.newtonbox.exceptions.notFound;

public class ExperimentNotFoundException extends BaseNotFoundException {
    public ExperimentNotFoundException(Long id) {
        super("Experiment not found with ID: " + id);
    }
}