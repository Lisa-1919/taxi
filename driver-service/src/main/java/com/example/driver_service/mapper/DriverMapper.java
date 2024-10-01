package com.example.driver_service.mapper;

import com.example.driver_service.dto.DriverDTO;
import com.example.driver_service.entity.Driver;
import org.mapstruct.Mapper;

@Mapper
public interface DriverMapper {

    Driver DriverDTOToDriver(DriverDTO driverDTO);
    DriverDTO DriverToDriverDTO(Driver driver);
}
