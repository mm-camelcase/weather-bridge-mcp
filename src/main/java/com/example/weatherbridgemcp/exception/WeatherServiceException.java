package com.example.weatherbridgemcp.exception;

/**
 * Thrown when the weather API call fails or returns an unexpected result.
 *
 * <p>Use {@link #clientError(String)} for failures caused by bad user input (city not found,
 * invalid parameters). Use the regular constructor for upstream API failures.
 */
public class WeatherServiceException extends RuntimeException {

    private final boolean clientError;

    public WeatherServiceException(String message) {
        super(message);
        this.clientError = false;
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
        this.clientError = false;
    }

    private WeatherServiceException(String message, boolean clientError) {
        super(message);
        this.clientError = clientError;
    }

    /** Factory for client-caused errors (bad city name, invalid parameters). */
    public static WeatherServiceException clientError(String message) {
        return new WeatherServiceException(message, true);
    }

    public boolean isClientError() {
        return clientError;
    }
}
