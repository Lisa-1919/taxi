package com.modsen.rating.dto

import com.modsen.rating.util.UserType
import java.util.UUID
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseRate @JsonCreator constructor(
    @JsonProperty("id") val id: Long,
    @JsonProperty("userId") val userId: UUID,
    @JsonProperty("userType") val userType: UserType,
    @JsonProperty("rideId") val rideId: Long,
    @JsonProperty("rate") val rate: Double,
    @JsonProperty("rideCommentary") val rideCommentary: String
)