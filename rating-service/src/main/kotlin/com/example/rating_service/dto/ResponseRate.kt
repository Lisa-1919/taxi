package com.example.rating_service.dto

data class ResponseRate(
    val id: Long,
    val driverId: Long,
    val passengerId: Long,
    val rideId: Long,
    val rate: Double,
    val rideCommentary: String
)
