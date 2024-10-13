package com.example.rating_service.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor

@Table(name = "rate")
@Entity
class Rate (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "driver_id")
    var driverId: Long,

    @Column(name = "passenger_id")
    var passengerId: Long,

    @Column(name = "ride_id")
    var rideId: Long,

    @Column(name = "rate")
    var rate: Double,

    @Column(name = "ride_commentary")
    var rideCommentary: String
) {
    constructor() : this(0L, 0L, 0L, 0L, 0.0, "")
}