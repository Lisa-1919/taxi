package com.modsen.storage_service.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionMessages {

    INVALID_FILE_FORMAT("Invalid file format. Allowed formats: %s"),
    AVATAR_NOT_FOUND("Avatar not found"),
    UNKNOWN_ERROR("Unknown error");

    private final String message;

    public String format(Object... args) {
        return String.format(message, args);
    }

}
