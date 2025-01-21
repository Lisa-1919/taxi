package com.modsen.driver.dto;

import java.util.UUID;

public record ResponseDriver(

        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String sex,
        ResponseCar carDto,
        Boolean isDeleted

) {
}