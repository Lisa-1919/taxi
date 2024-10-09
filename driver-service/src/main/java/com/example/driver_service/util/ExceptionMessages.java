package com.example.driver_service.util;

public enum ExceptionMessages {

    DRIVER_NOT_FOUND("Driver with id '%s' not found"),
    DUPLICATE_DRIVER_ERROR("A driver with that email or phone number already exists"),

    CAR_NOT_FOUND ("Car with id '%s' not found"),
    DUPLICATE_CAR_ERROR("A car with license plate '%s' already exists");

    private final String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
