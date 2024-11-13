package com.modsen.driver.dto;

public record ResponseDriver(

        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String sex,
        ResponseCar carDto,
        Boolean isDeleted

) {
}