package com.modsen.ride.config;

import com.modsen.ride.mapper.RideMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public RideMapper rideMapper() {
        return Mappers.getMapper(RideMapper.class);
    }

}
