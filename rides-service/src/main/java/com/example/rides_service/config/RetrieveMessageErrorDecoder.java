package com.example.rides_service.config;

import com.example.rides_service.util.ExceptionMessages;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    private ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        String errorMessage = ExceptionMessages.UNKNOWN_ERROR.toString();
        if (response.body() != null) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                errorMessage = new BufferedReader(new InputStreamReader(bodyIs))
                        .lines()
                        .collect(Collectors.joining("\n"));
            } catch (IOException e) {
                log.error("Error reading response body: {}", e.getMessage());
                return new Exception(ExceptionMessages.UNABLE_TO_READ_ERROR_RESPONSE.format());
            }
        }
        return switch (response.status()) {
            case 400 -> new BadRequestException(errorMessage);
            case 404 -> new EntityNotFoundException(errorMessage);
            default -> errorDecoder.decode(s, response);
        };
    }
}
