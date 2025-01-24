package com.modsen.account.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionMessages {

    UNABLE_TO_READ_ERROR_RESPONSE("Unable to read error response"),
    UNABLE_TO_REACH_PASSENGER_SERVICE("Unable to reach Passenger Service. Please try again later"),
    UNABLE_TO_REACH_DRIVER_SERVICE("Unable to reach Driver Service. Please try again later"),
    ACCESS_DENIED("Access denied"),
    ROLE_DOES_NOT_EXIST("Role '%s' does not exist in the realm"),
    CREATE_USER_ERROR("Failed to create user"),
    DELETE_USER_ERROR("Failed to dalete user"),
    UNKNOWN_ERROR("Unknown error");

    private String message;

    public String format(Object... args) {
        return String.format(message, args);
    }

}
