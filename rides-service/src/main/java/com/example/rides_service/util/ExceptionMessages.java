package com.example.rides_service.util;

public enum ExceptionMessages {

    RIDE_NOT_FOUND("Ride with id '%s' not found"),
    RIDE_NOT_FOUND_FOR_DRIVER("Ride with id '%s' not found for driver with id '%s'"),
    RIDE_NOT_FOUND_FOR_PASSENGER("Ride with id '%s' not found for passenger with id '%s'"),
    INVALID_STATUS_TRANSITION("Invalid status transition from '%s' to '%s'"),
    UNABLE_TO_READ_ERROR_RESPONSE("Unable to read error response"),
    UNKNOWN_ERROR("Unknown error");
    private String message;

    ExceptionMessages(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
