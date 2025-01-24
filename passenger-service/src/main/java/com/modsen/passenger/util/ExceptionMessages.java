package com.modsen.passenger.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionMessages {

    PASSENGER_NOT_FOUND ("Passenger with id '%s' not found"),
    DUPLICATE_PASSENGER_ERROR("A passenger with %s '%s' already exists"),
    ACCESS_DENIED("Access denied");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }

}
