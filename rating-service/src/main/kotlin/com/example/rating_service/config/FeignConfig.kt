package com.example.rating_service.config

import com.example.rating_service.client.RetrieveMessageErrorDecoder
import com.fasterxml.jackson.databind.ObjectMapper
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FeignConfig {
    @Bean
    open fun customErrorDecoder(): ErrorDecoder {
        return RetrieveMessageErrorDecoder()
    }
}