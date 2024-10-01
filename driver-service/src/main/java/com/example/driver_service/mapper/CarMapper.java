package com.example.driver_service.mapper;

import com.example.driver_service.dto.CarDTO;
import com.example.driver_service.entity.Car;
import org.mapstruct.Mapper;

@Mapper
public interface CarMapper {

    Car CarDTOToCar(CarDTO carDTO);
    CarDTO CarToCarDTO(Car car);
}
