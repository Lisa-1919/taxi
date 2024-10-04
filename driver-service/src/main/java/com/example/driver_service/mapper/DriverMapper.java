package com.example.driver_service.mapper;

import com.example.driver_service.dto.DriverDto;
import com.example.driver_service.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface DriverMapper {

    Driver driverDtoToDriver(DriverDto driverDto);
    DriverDto driverToDriverDto(Driver driver);
    void updateDriverFromDriverDto(DriverDto driverDto, @MappingTarget Driver driver);
}
