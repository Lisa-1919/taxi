package com.example.rating_service.exception

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: RuntimeException): ResponseEntity<String> {
        log.error("Error: ${ex.message}")

        return ResponseEntity("Error: ${ex.message}", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleRuntimeException(ex: Exception): ResponseEntity<String> {
        log.error("Exception caught: ${ex.message}")

        return ResponseEntity("Error occurred: ${ex.message}", HttpStatus.BAD_REQUEST)
    }

}