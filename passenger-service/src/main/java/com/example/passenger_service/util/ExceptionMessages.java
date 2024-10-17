package com.example.passenger_service.util;

public enum ExceptionMessages {

    PASSENGER_NOT_FOUND ("Passenger with id '%s' not found"),
    DUPLICATE_PASSENGER_ERROR("A passenger with %s '%s' already exists");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
