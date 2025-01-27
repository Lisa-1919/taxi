package com.modsen.rating.dto

import com.modsen.rating.util.UserType
import java.util.UUID

data class ResponseRate(
    val id: Long,
    val userId: UUID,
    val userType: UserType?,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
