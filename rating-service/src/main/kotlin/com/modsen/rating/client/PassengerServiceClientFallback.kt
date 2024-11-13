package com.modsen.rating.client

import com.modsen.rating.util.ExceptionMessages
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class PassengerServiceClientFallback: com.modsen.rating.client.PassengerServiceClient {
    override fun doesPassengerExist(passengerId: Long): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE)
    }
}