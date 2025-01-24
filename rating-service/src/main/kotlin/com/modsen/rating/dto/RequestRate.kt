package com.modsen.rating.dto

import com.modsen.rating.util.UserType
import java.util.UUID

data class RequestRate(
    val userId: UUID,
    val userType: UserType,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
