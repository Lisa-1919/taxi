package com.example.passenger_service.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(RuntimeException ex, WebRequest request) {
        log.error("Error: {}. Request: {}", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Error: {}. Request: {}", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}. Request: {}", ex.getMessage(), request.getDescription(false));

        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.append("Field '").append(error.getField())
                    .append("' ").append(error.getDefaultMessage())
                    .append(". Rejected value: ").append(error.getRejectedValue())
                    .append("; ");
        });

        return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Error: {}. Request: {}", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>("There was an error on the server. Try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
