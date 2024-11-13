package com.modsen.rating.dto

import com.modsen.rating.util.UserType

data class RequestRate(
    val userId: Long,
    val userType: UserType,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
