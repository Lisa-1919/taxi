package com.modsen.rating.exception

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        ex: RuntimeException,
        request: WebRequest
    ): ResponseEntity<String> {
        log.error("Error: {}. Request: {}", ex.message, request.getDescription(false))

        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        ex: DataIntegrityViolationException,
        request: WebRequest
    ): ResponseEntity<String?> {
        log.error("Error: {}. Request: {}", ex.message, request.getDescription(false))
        return ResponseEntity(ex.message, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(Exception::class)
    fun handleRuntimeException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<String> {
        log.error("Error: {}. Request: {}", ex.message, request.getDescription(false))
        return ResponseEntity("There was an error on the server. Try again later.", HttpStatus.INTERNAL_SERVER_ERROR)
    }

}