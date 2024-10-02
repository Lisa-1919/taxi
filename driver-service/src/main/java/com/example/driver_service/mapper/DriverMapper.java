package com.example.driver_service.mapper;

import com.example.driver_service.dto.DriverDTO;
import com.example.driver_service.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DriverMapper {

    Driver driverDTOToDriver(DriverDTO driverDTO);
    DriverDTO driverToDriverDTO(Driver driver);
    void updateDriverFromDriverDTO(DriverDTO driverDTO, @MappingTarget Driver driver);
}
