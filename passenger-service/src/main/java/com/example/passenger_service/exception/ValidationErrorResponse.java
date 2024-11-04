package com.example.passenger_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {

}
