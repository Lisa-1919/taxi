package com.modsen.rating.client

import com.modsen.rating.util.ExceptionMessages
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class DriverServiceClientFallback: com.modsen.rating.client.DriverServiceClient {
    override fun doesDriverExist(driverId: Long): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_DRIVER_SERVICE)
    }
}