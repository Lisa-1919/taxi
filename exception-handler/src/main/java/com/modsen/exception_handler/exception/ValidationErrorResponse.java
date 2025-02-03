package com.modsen.exception_handler.exception;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {

}
