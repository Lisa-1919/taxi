package com.example.rides_service.util;

import com.example.rides_service.entity.Ride;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RideBuilder {
    private Long rideId = RideTestEntityUtils.DEFAULT_RIDE_ID;
    private Long driverId = null;
    private Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;
    private String fromAddress = RideTestEntityUtils.DEFAULT_FROM_ADDRESS;
    private String toAddress = RideTestEntityUtils.DEFAULT_TO_ADDRESS;
    private RideStatuses status = RideStatuses.CREATED;
    private LocalDateTime dateTime = RideTestEntityUtils.DEFAULT_DATETIME;
    private BigDecimal price = RideTestEntityUtils.DEFAULT_PRICE;

    public RideBuilder rideId(Long rideId) {
        this.rideId = rideId;
        return this;
    }

    public RideBuilder driverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public RideBuilder passengerId(Long passengerId) {
        this.passengerId = passengerId;
        return this;
    }

    public RideBuilder fromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    public RideBuilder toAddress(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    public RideBuilder status(RideStatuses status) {
        this.status = status;
        return this;
    }

    public RideBuilder dateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public RideBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Ride build() {
        return new Ride(rideId, driverId, passengerId, fromAddress, toAddress, status, dateTime, price);
    }
}
