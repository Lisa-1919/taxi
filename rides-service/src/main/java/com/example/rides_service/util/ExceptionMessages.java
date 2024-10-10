package com.example.rides_service.util;

public enum ExceptionMessages {

    RIDE_NOT_FOUND("Ride with id '%s' not found"),
    INVALID_STATUS_TRANSITION("Invalid status transition from '%s' to '%s'"),
    DRIVER_NOT_FOUND("Driver with id '%s' not found"),
    PASSENGER_NOT_FOUND("Passenger with id '%s' not found");

    private String message;

    ExceptionMessages(String message){
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }

}
