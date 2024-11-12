package com.example.rides_service.exception;

import java.util.List;

public record ValidationErrorResponse(
        List<Violation> violations
) {
}