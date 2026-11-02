package com.newtonbox.exceptions.conflict;

public class ParticipantAlreadyExistsException extends RuntimeException {
    public ParticipantAlreadyExistsException(Long experimentId, Long userId) {
        super("Participant already exists in Experiment with ID: " + experimentId + " for User with ID: " + userId);
    }
}