package com.modsen.rating.entity

import com.modsen.rating.util.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Table(name = "rate")
@Entity
class Rate(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "user_id")
    var userId: UUID,

    @Column(name = "user_type")
    @Enumerated(EnumType.STRING)
    var userType: UserType,

    @Column(name = "ride_id")
    var rideId: Long,

    @Column(name = "rate")
    var rate: Double,

    @Column(name = "ride_commentary")
    var rideCommentary: String,

    ) {
    constructor() : this(0L, UUID.randomUUID(), UserType.PASSENGER,0L, 0.0, "")
}