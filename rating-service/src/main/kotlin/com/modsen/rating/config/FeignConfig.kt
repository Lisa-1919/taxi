package com.modsen.rating.config

import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FeignConfig {
    @Bean
    open fun customErrorDecoder(): ErrorDecoder {
        return com.modsen.rating.client.RetrieveMessageErrorDecoder()
    }
}