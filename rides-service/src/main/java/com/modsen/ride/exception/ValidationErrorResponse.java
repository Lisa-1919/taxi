package com.modsen.ride.exception;

import java.util.List;

public record ValidationErrorResponse(
        List<Violation> violations
) {
}