package com.modsen.rating.client

import com.modsen.rating.util.ExceptionMessages
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class PassengerServiceClientFallback: PassengerServiceClient {
    override fun doesPassengerExist(passengerId: UUID): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE)
    }
}