package com.modsen.rating.config

import feign.RequestInterceptor
import feign.RequestTemplate
import feign.codec.ErrorDecoder
import org.apache.http.HttpHeaders
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Configuration
open class FeignConfig {
    @Bean
    open fun customErrorDecoder(): ErrorDecoder {
        return com.modsen.rating.client.RetrieveMessageErrorDecoder()
    }

    @Bean
    open fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate: RequestTemplate ->
            val requestAttributes =
                RequestContextHolder.getRequestAttributes()
            if (requestAttributes is ServletRequestAttributes) {
                val currentRequest =
                    (requestAttributes as ServletRequestAttributes).request
                val token = currentRequest.getHeader(HttpHeaders.AUTHORIZATION)
                if (token != null && token.startsWith("Bearer ")) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, token)
                }
            }
        }
    }

}

