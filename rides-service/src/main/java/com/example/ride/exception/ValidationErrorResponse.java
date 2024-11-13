package com.example.ride.exception;

import java.util.List;

public record ValidationErrorResponse(
        List<Violation> violations
) {
}