package com.example.weatherbridgemcp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents the forecast response from the OpenWeatherMap /forecast endpoint.
 * Contains a list of 3-hour forecast slots (up to 40 entries = 5 days).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastData {

    private List<ForecastItem> list;
    private City city;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        private long dt;
        private MainData main;
        private List<WeatherCondition> weather;
        private Wind wind;

        @JsonProperty("dt_txt")
        private String dtTxt;

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
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class WeatherCondition {
            private String main;
            private String description;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Wind {
            private double speed;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private String name;
        private String country;
    }
}
