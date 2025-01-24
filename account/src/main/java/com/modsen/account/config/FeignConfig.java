package com.modsen.account.config;

import com.modsen.account.util.FeignErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final Keycloak keycloak;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String accessToken = keycloak
                    .tokenManager()
                    .getAccessToken()
                    .getToken();
            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

}
