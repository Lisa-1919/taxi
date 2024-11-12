package com.example.rides_service.util;

import com.example.rides_service.dto.PagedResponseRideList;
import com.example.rides_service.dto.RequestChangeStatus;
import com.example.rides_service.dto.RequestRide;
import com.example.rides_service.dto.ResponseRide;
import com.example.rides_service.entity.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RideTestEntityUtils {
    public static final Long DEFAULT_RIDE_ID = 1L;
    public static final Long DEFAULT_DRIVER_ID = 1L;
    public static final Long DEFAULT_PASSENGER_ID = 1L;
    public static final String DEFAULT_FROM_ADDRESS = "fromAddress";
    public static final String DEFAULT_TO_ADDRESS = "toAddress";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("10.0");
    public static final LocalDateTime DEFAULT_DATETIME = LocalDateTime.of(2024, 11, 5, 12, 5);
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final long DEFAULT_TOTAL_ELEMENTS = 1L;
    public static final int DEFAULT_TOTAL_PAGES = 1;

    public static RideBuilder createTestRide() {
        return new RideBuilder();
    }

    public static RequestRideBuilder createTestRequestRide() {
        return new RequestRideBuilder();
    }

    public static ResponseRideBuilder createTestResponseRide() {
        return new ResponseRideBuilder();
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
