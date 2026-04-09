package com.example.weatherbridgemcp.exception;

/**
 * Thrown when the weather API call fails or returns an unexpected result.
 */
public class WeatherServiceException extends RuntimeException {

    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
