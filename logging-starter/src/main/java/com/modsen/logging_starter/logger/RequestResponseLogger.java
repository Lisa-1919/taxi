package com.modsen.logging_starter.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.logging_starter.util.ExceptionMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Aspect
public class RequestResponseLogger {

    private final ObjectMapper objectMapper;

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logHttpRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        HttpServletResponse httpResponse =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        String requestBody = getRequestBody(joinPoint);

        log.info("Request: Method: {}, URI: {}, Body: {}",
                request.getMethod(),
                request.getRequestURI(),
                requestBody
        );

        Object response = null;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            response = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception: Method: {}, URI: {} failed with exception message: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage()
            );
            throw e;
        } finally {
            stopWatch.stop();
        }

        String responseBody = convertObjectToJson(response);
        int status = (httpResponse != null) ? httpResponse.getStatus() : 0;

        log.info("Response: Method: {}, URI: {}, Status {} - Body: {} Time Taken: {} ms",
                request.getMethod(),
                request.getRequestURI(),
                status,
                responseBody,
                stopWatch.getTotalTimeMillis()
        );

        return response;
    }

    private String getRequestBody(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0) {
            try {
                return Arrays.stream(args)
                        .map(this::convertObjectToJson)
                        .reduce((arg1, arg2) -> arg1 + ", " + arg2)
                        .orElse("");
            } catch (Exception e) {
                log.error(ExceptionMessages.REQUEST_SERIALIZATION_ERROR, e);
            }
        }

        return "";
    }

    private String convertObjectToJson(Object object) {
        if (object == null)
            return "";

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.OBJECT_SERIALIZATION_ERROR, e);

            return ExceptionMessages.OBJECT_SERIALIZATION_ERROR;
        }
    }
}

