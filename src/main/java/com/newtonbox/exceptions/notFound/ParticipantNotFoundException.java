package com.newtonbox.exceptions.notFound;

public class ParticipantNotFoundException extends BaseNotFoundException {
    public ParticipantNotFoundException(Long id) {
        super("Participant not found with ID: " + id);
    }
}
