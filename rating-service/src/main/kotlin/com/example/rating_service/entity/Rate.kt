package com.example.rating_service.entity

import com.example.rating_service.util.RateStatus
import com.example.kafka.util.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "rate")
@Entity
class Rate(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "user_id")
    var userId: Long,

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    var userType: UserType,

    @Column(name = "ride_id")
    var rideId: Long,

    @Column(name = "rate")
    var rate: Double,

    @Column(name = "ride_commentary")
    var rideCommentary: String,

    @Column(name = "rate_status")
    @Enumerated(EnumType.STRING)
    var status: RateStatus
) {
    constructor() : this(0L, 0L, UserType.PASSENGER,0L, 0.0, "", RateStatus.PENDING)
}