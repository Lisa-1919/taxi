package com.example.driver_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarDTO {
    private Long id;

    private String licensePlate;

    private String mark;

    private String colour;

    private boolean isDeleted;
}
