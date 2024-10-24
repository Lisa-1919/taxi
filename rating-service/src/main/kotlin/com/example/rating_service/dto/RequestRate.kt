package com.example.rating_service.dto

import com.example.kafka.util.UserType

data class RequestRate(
    val userId: Long,
    val userType: UserType,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
