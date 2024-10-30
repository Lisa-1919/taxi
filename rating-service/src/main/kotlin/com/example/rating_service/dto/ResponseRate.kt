package com.example.rating_service.dto

import com.example.rating_service.util.UserType

data class ResponseRate(
    val id: Long,
    val userId: Long,
    val userType: UserType?,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
