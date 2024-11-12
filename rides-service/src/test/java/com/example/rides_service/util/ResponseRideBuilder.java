package com.example.rides_service.util;

import com.example.rides_service.dto.ResponseRide;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResponseRideBuilder {
    private Long rideId = RideTestEntityUtils.DEFAULT_RIDE_ID;
    private Long driverId = null;
    private Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;
    private String fromAddress = RideTestEntityUtils.DEFAULT_FROM_ADDRESS;
    private String toAddress = RideTestEntityUtils.DEFAULT_TO_ADDRESS;
    private RideStatuses status = RideStatuses.CREATED;
    private LocalDateTime dateTime = RideTestEntityUtils.DEFAULT_DATETIME;
    private BigDecimal price = RideTestEntityUtils.DEFAULT_PRICE;

    public ResponseRideBuilder rideId(Long rideId) {
        this.rideId = rideId;
        return this;
    }

    public ResponseRideBuilder driverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public ResponseRideBuilder passengerId(Long passengerId) {
        this.passengerId = passengerId;
        return this;
    }

    public ResponseRideBuilder fromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    public ResponseRideBuilder toAddress(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    public ResponseRideBuilder status(RideStatuses status) {
        this.status = status;
        return this;
    }

    public ResponseRideBuilder dateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public ResponseRideBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ResponseRide build() {
        return new ResponseRide(rideId, driverId, passengerId, fromAddress, toAddress, status, dateTime, price);
    }
}
