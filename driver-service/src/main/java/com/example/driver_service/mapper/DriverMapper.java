package com.example.driver_service.mapper;

import com.example.driver_service.dto.DriverDto;
import com.example.driver_service.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = CarMapper.class)
public interface DriverMapper {

    @Mapping(source = "carDto", target = "car")
    Driver driverDtoToDriver(DriverDto driverDto);

    @Mapping(source = "car", target = "carDto")
    DriverDto driverToDriverDto(Driver driver);

    @Mapping(source = "carDto", target = "car")
    void updateDriverFromDriverDto(DriverDto updatedDriverDto, @MappingTarget Driver driverFromDB);
}