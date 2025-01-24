package com.modsen.account.util;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Unknown error occurred";
        if (response.body() != null) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                errorMessage = new BufferedReader(new InputStreamReader(bodyIs))
                        .lines()
                        .collect(Collectors.joining("\n"));
            } catch (IOException e) {
                log.error("Error reading response body: {}", e.getMessage());
            }
        }
        log.error("Feign client error: [{}] {}", response.status(), errorMessage);

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(errorMessage);
            case 404 -> new EntityNotFoundException(errorMessage);
            case 409 -> new DataIntegrityViolationException(errorMessage);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
