package com.modsen.passenger.util;

import com.modsen.passenger.dto.CreatePassengerRequest;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public class PassengerTestEntityUtils {

    public static final UUID DEFAULT_PASSENGER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final String DEFAULT_EMAIL = "john@example.com";
    public static final String DEFAULT_PHONE_NUMBER = "1234567890";
    public static final boolean DEFAULT_IS_DELETED = false;

    public static final String NEW_EMAIL = "new.john@example.com";
    public static final String NEW_PHONE_NUMBER = "1234567000";

    public static final String INVALID_EMAIL = "invalid";
    public static final String INVALID_PHONE_NUMBER = "invalid";

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_TOTAL_ELEMENTS = 1;
    public static final int DEFAULT_TOTAL_PAGES = 1;
    public static final boolean DEFAULT_LAST_PAGE = true;


    public static Passenger createTestPassenger() {
        return new Passenger(
                DEFAULT_PASSENGER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_IS_DELETED
        );
    }

    public static RequestPassenger createTestRequestPassenger() {
        return new RequestPassenger(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER
        );
    }

    public static ResponsePassenger createTestResponsePassenger() {
        return new ResponsePassenger(
                DEFAULT_PASSENGER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_IS_DELETED
        );
    }

    public static Passenger createUpdatedPassenger() {
        return new Passenger(
                DEFAULT_PASSENGER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                NEW_EMAIL,
                NEW_PHONE_NUMBER,
                DEFAULT_IS_DELETED
        );
    }

    public static RequestPassenger createUpdateRequestPassenger() {
        return new RequestPassenger(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                NEW_EMAIL,
                NEW_PHONE_NUMBER
        );
    }

    public static ResponsePassenger createUpdatedResponsePassenger() {
        return new ResponsePassenger(
                DEFAULT_PASSENGER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                NEW_EMAIL,
                NEW_PHONE_NUMBER,
                DEFAULT_IS_DELETED
        );
    }

    public static RequestPassenger createInvalidRequestPassenger() {
        return new RequestPassenger(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                INVALID_EMAIL,
                INVALID_PHONE_NUMBER
        );
    }

    public static PageRequest createDefaultPageRequest() {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    public static Page<Passenger> createDefaultPassengerPage(List<Passenger> passengers) {
        return new PageImpl<>(passengers, createDefaultPageRequest(), DEFAULT_TOTAL_ELEMENTS);
    }

    public static PagedResponsePassengerList createDefaultPagedResponsePassengerList(List<ResponsePassenger> passengers) {
        return new PagedResponsePassengerList(
                passengers,
                DEFAULT_PAGE_NUMBER,
                DEFAULT_PAGE_SIZE,
                DEFAULT_TOTAL_ELEMENTS,
                DEFAULT_TOTAL_PAGES,
                DEFAULT_LAST_PAGE
        );
    }

    public static CreatePassengerRequest createPassengerRequest() {
        return new CreatePassengerRequest(
                DEFAULT_PASSENGER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER
        );
    }

    public static CreatePassengerRequest createInvalidCreateRequestPassenger() {
        return new CreatePassengerRequest(
                DEFAULT_PASSENGER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                INVALID_EMAIL,
                INVALID_PHONE_NUMBER
        );
    }

}
