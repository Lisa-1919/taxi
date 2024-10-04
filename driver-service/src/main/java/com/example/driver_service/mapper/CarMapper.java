package com.example.driver_service.mapper;

import com.example.driver_service.dto.CarDto;
import com.example.driver_service.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface CarMapper {

    Car carDtoToCar(CarDto carDto);
    CarDto carToCarDto(Car car);
    void updateCarFromCarDto(CarDto carDto, @MappingTarget Car car);
}
