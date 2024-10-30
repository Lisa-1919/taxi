package com.example.rating_service.client

import com.example.rating_service.util.ExceptionMessages
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class PassengerServiceClientFallback: PassengerServiceClient {
    override fun doesPassengerExist(passengerId: Long): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE)
    }
}