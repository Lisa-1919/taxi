package com.modsen.ride.exception;

public record Violation(
        String fieldName,
        String message
) {
}
