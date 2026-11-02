package com.newtonbox.exceptions.conflict;

public class ExperimentTitleConflictException extends RuntimeException {

    public ExperimentTitleConflictException(String title) {
        super("Experiment with title '" + title + "' already exists.");
    }

}