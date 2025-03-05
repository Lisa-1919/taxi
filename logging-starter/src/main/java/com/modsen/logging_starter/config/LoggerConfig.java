package com.modsen.logging_starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.logging_starter.logger.RequestResponseLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
public class LoggerConfig {

    @Bean
    public RequestResponseLogger requestResponseLogger(ObjectMapper objectMapper) {
        return new RequestResponseLogger(objectMapper);
    }
}
