package com.example.ride.exception;

public record Violation(
        String fieldName,
        String message
) {
}
