package com.modsen.rating.client

import com.modsen.rating.util.ExceptionMessages
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RideServiceClientFallback: RideServiceClient {
    override fun doesRideExistForDriver(rideId: Long, driverId: UUID): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_RIDES_SERVICE)
    }

    override fun doesRideExistForPassenger(rideId: Long, passengerId: UUID): ResponseEntity<Boolean> {
        throw RuntimeException(ExceptionMessages.UNABLE_TO_REACH_RIDES_SERVICE)
    }
}