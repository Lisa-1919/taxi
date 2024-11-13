package com.modsen.passenger.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {

}
