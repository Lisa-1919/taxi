package com.example.rides_service.util;

import com.example.rides_service.dto.RequestRide;

import java.math.BigDecimal;

public class RequestRideBuilder {
    private Long driverId = null;
    private Long passengerId = RideTestEntityUtils.DEFAULT_PASSENGER_ID;
    private String fromAddress = RideTestEntityUtils.DEFAULT_FROM_ADDRESS;
    private String toAddress = RideTestEntityUtils.DEFAULT_TO_ADDRESS;
    private BigDecimal price = RideTestEntityUtils.DEFAULT_PRICE;

    public RequestRideBuilder driverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public RequestRideBuilder passengerId(Long passengerId) {
        this.passengerId = passengerId;
        return this;
    }

    public RequestRideBuilder fromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
        return this;
    }

    public RequestRideBuilder toAddress(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    public RequestRideBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public RequestRide build() {
        return new RequestRide(driverId, passengerId, fromAddress, toAddress, price);
    }
}
