package com.modsen.rating.config

import feign.RequestInterceptor
import feign.RequestTemplate
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContextHolder

@Configuration
open class FeignConfig {
    @Bean
    open fun customErrorDecoder(): ErrorDecoder {
        return com.modsen.rating.client.RetrieveMessageErrorDecoder()
    }

    @Bean
    open fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate ->
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication != null && authentication.credentials is String) {
                val token = authentication.credentials as String
                requestTemplate.header("Authorization", "Bearer $token")
            }
        }
    }

}

