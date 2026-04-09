package com.example.weatherbridgemcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents the current weather response from the OpenWeatherMap /weather endpoint.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

    private String name;
    private List<WeatherCondition> weather;
    private MainData main;
    private Wind wind;
    private Clouds clouds;
    private Sys sys;
    private Integer visibility;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherCondition {
        private long id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        private double temp;

        @JsonProperty("feels_like")
        private double feelsLike;

        @JsonProperty("temp_min")
        private double tempMin;

        @JsonProperty("temp_max")
        private double tempMax;

        private int humidity;
        private int pressure;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private double speed;
        private Integer deg;
        private Double gust;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {
        private int all;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private String country;
        private Long sunrise;
        private Long sunset;
    }
}
