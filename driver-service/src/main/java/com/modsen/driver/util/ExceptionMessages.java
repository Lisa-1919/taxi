package com.modsen.driver.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionMessages {

    DRIVER_NOT_FOUND("Driver with id '%s' not found"),
    DUPLICATE_DRIVER_ERROR("A driver with %s '%s' already exists"),
    CAR_NOT_FOUND("Car with id '%s' not found"),
    DUPLICATE_CAR_ERROR("A car with %s '%s' already exists"),
    ACCESS_DENIED("Access denied");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }

}
