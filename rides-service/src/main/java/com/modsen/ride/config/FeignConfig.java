package com.modsen.ride.config;

import feign.RequestInterceptor;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    public RetrieveMessageErrorDecoder retrieveMessageErrorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (authentication != null && authentication.getCredentials() instanceof String token) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        };
    }
}
