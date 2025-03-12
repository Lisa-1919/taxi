package com.modsen.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/v1/account/login", "/api/v1/account/register").permitAll()
                        .pathMatchers("/api/v1/account/**").authenticated()
                        .pathMatchers("/api/v1/rides/**").authenticated()
                        .pathMatchers("/api/v1/rating/**").authenticated()
                        .pathMatchers("/api/v1/avatars/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/passengers").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/v1/drivers").authenticated()
                        .anyExchange().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                )
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

}
