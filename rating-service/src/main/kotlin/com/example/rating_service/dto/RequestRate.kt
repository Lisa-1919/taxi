package com.example.rating_service.dto

data class RequestRate(
    val driverId: Long,
    val passengerId: Long,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
