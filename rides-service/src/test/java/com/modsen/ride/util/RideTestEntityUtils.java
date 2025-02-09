package com.modsen.ride.util;

import com.modsen.ride.dto.PagedResponseRideList;
import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.ResponseRide;
import com.modsen.ride.entity.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RideTestEntityUtils {
    public static final Long DEFAULT_RIDE_ID = 1L;
    public static final UUID DEFAULT_DRIVER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID DEFAULT_PASSENGER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final String DEFAULT_FROM_ADDRESS = "fromAddress";
    public static final String DEFAULT_TO_ADDRESS = "toAddress";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("10.0");
    public static final LocalDateTime DEFAULT_DATETIME = LocalDateTime.of(2024, 11, 5, 12, 5);
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final long DEFAULT_TOTAL_ELEMENTS = 1L;
    public static final int DEFAULT_TOTAL_PAGES = 1;

    public static Ride.RideBuilder createTestRide() {
        return Ride.builder()
                .id(DEFAULT_RIDE_ID)
                .driverId(null)
                .passengerId(DEFAULT_PASSENGER_ID)
                .fromAddress(DEFAULT_FROM_ADDRESS)
                .toAddress(DEFAULT_TO_ADDRESS)
                .cost(DEFAULT_PRICE)
                .orderDateTime(DEFAULT_DATETIME)
                .rideStatus(RideStatuses.CREATED);
    }

    public static RequestRide.RequestRideBuilder createTestRequestRide() {
        return RequestRide.builder()
                .driverId(DEFAULT_DRIVER_ID)
                .passengerId(DEFAULT_PASSENGER_ID)
                .fromAddress(DEFAULT_FROM_ADDRESS)
                .toAddress(DEFAULT_TO_ADDRESS)
                .cost(DEFAULT_PRICE);
    }

    public static ResponseRide.ResponseRideBuilder createTestResponseRide() {
        return ResponseRide.builder()
                .id(DEFAULT_RIDE_ID)
                .driverId(null)
                .passengerId(DEFAULT_PASSENGER_ID)
                .fromAddress(DEFAULT_FROM_ADDRESS)
                .toAddress(DEFAULT_TO_ADDRESS)
                .cost(DEFAULT_PRICE)
                .orderDateTime(DEFAULT_DATETIME)
                .rideStatus(RideStatuses.CREATED);
    }

    public static RequestChangeStatus createChangeStatusRequest(RideStatuses rideStatuses) {
        return new RequestChangeStatus(rideStatuses);
    }

    public static Pageable createDefaultPageRequest() {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    public static Page<Ride> createDefaultRidePage(List<Ride> rides) {
        return new PageImpl<>(rides, createDefaultPageRequest(), rides.size());
    }

    public static PagedResponseRideList createPagedResponseRideList(List<ResponseRide> rides) {
        return new PagedResponseRideList(
                rides,
                DEFAULT_PAGE_NUMBER,
                DEFAULT_PAGE_SIZE,
                DEFAULT_TOTAL_ELEMENTS,
                DEFAULT_TOTAL_PAGES,
                true
        );
    }
}
