package com.example.weatherbridgemcp.controller;

import com.example.weatherbridgemcp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Optional REST endpoints for health checks and manual tool testing.
 * These are not part of the MCP protocol — they exist purely for local development.
 */
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /** Quick health-check used by Docker and CI. */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "weather-bridge-mcp"));
    }

    /** Fetch current weather — mirrors the {@code getCurrentWeather} MCP tool. */
    @GetMapping("/current/{city}")
    public ResponseEntity<String> currentWeather(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getCurrentWeather(city));
    }

    /** Fetch a forecast — mirrors the {@code getForecast} MCP tool. */
    @GetMapping("/forecast/{city}")
    public ResponseEntity<String> forecast(
            @PathVariable String city,
            @RequestParam(defaultValue = "3") int days) {
        return ResponseEntity.ok(weatherService.getForecast(city, days));
    }
}
