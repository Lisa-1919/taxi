package com.modsen.rating.client

import com.modsen.rating.util.ExceptionMessages
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class DriverServiceClientFallback: DriverServiceClient {
    override fun doesDriverExist(driverId: UUID): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_DRIVER_SERVICE)
    }
}