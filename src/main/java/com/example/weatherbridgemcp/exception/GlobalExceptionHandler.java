package com.example.weatherbridgemcp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates application exceptions into RFC 7807 Problem Detail responses
 * for the optional REST controller endpoints.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WeatherServiceException.class)
    public ProblemDetail handleWeatherServiceException(WeatherServiceException ex) {
        log.warn("Weather service error: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        detail.setTitle("Weather Service Error");
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid request: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Invalid Request");
        detail.setDetail(ex.getMessage());
        return detail;
    }
}
