package com.example.weatherbridgemcp.service;

import com.example.weatherbridgemcp.exception.WeatherServiceException;
import com.example.weatherbridgemcp.model.ForecastData;
import com.example.weatherbridgemcp.model.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Provides MCP tools for retrieving weather data from the OpenWeatherMap API.
 *
 * <p>Methods annotated with {@link Tool} are automatically discovered by the
 * {@code MethodToolCallbackProvider} registered in the application context and
 * exposed to AI agents via the MCP SSE endpoint.
 */
@Slf4j
@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public WeatherService(
            RestTemplate restTemplate,
            @Value("${openweathermap.api-key}") String apiKey,
            @Value("${openweathermap.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * MCP tool: returns the current weather for the given city.
     *
     * @param city city name, e.g. "London" or "New York"
     * @return formatted weather summary
     */
    @Tool(description = """
            Get the current weather conditions for a city.
            Returns temperature (°C), feels-like temperature, weather description,
            humidity, wind speed, pressure, and visibility.
            Example cities: "London", "New York", "Tokyo", "Sydney".
            """)
    public String getCurrentWeather(String city) {
        log.info("Fetching current weather for: {}", city);
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/weather")
                    .queryParam("q", city)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();

            WeatherData data = restTemplate.getForObject(url, WeatherData.class);
            if (data == null) {
                throw new WeatherServiceException("No data returned for city: " + city);
            }
            return formatCurrentWeather(data);
        } catch (HttpClientErrorException.NotFound e) {
            throw new WeatherServiceException("City not found: \"" + city + "\". Check the spelling or try a larger nearby city.");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new WeatherServiceException("Invalid API key. Please update openweathermap.api-key in application.properties.");
        } catch (HttpClientErrorException e) {
            throw new WeatherServiceException("Weather API error (" + e.getStatusCode() + "): " + e.getMessage());
        }
    }

    /**
     * MCP tool: returns a multi-day weather forecast for the given city.
     *
     * @param city city name, e.g. "Paris"
     * @param days number of days to forecast (1–5)
     * @return formatted daily forecast summary
     */
    @Tool(description = """
            Get a multi-day weather forecast for a city.
            Provide the city name and the number of days (1 to 5).
            Returns a daily summary with temperature, min/max, and conditions.
            Example cities: "Paris", "Berlin", "São Paulo".
            """)
    public String getForecast(String city, int days) {
        log.info("Fetching {}-day forecast for: {}", days, city);
        if (days < 1 || days > 5) {
            throw new WeatherServiceException("Forecast days must be between 1 and 5, received: " + days);
        }
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/forecast")
                    .queryParam("q", city)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .queryParam("cnt", days * 8) // OpenWeatherMap provides 3-hour slots; 8 per day
                    .build()
                    .toUriString();

            ForecastData data = restTemplate.getForObject(url, ForecastData.class);
            if (data == null) {
                throw new WeatherServiceException("No forecast data returned for city: " + city);
            }
            return formatForecast(data, days);
        } catch (HttpClientErrorException.NotFound e) {
            throw new WeatherServiceException("City not found: \"" + city + "\". Check the spelling or try a larger nearby city.");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new WeatherServiceException("Invalid API key. Please update openweathermap.api-key in application.properties.");
        } catch (HttpClientErrorException e) {
            throw new WeatherServiceException("Weather API error (" + e.getStatusCode() + "): " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Formatting helpers
    // -------------------------------------------------------------------------

    private String formatCurrentWeather(WeatherData data) {
        WeatherData.MainData main = data.getMain();
        WeatherData.Wind wind = data.getWind();
        String condition = (data.getWeather() != null && !data.getWeather().isEmpty())
                ? capitalise(data.getWeather().get(0).getDescription())
                : "Unknown";
        String country = (data.getSys() != null) ? ", " + data.getSys().getCountry() : "";

        return String.format("""
                Current weather in %s%s:
                  Conditions:   %s
                  Temperature:  %.1f°C (feels like %.1f°C)
                  Min / Max:    %.1f°C / %.1f°C
                  Humidity:     %d%%
                  Wind:         %.1f m/s
                  Pressure:     %d hPa
                  Visibility:   %,d m
                """,
                data.getName(), country,
                condition,
                main.getTemp(), main.getFeelsLike(),
                main.getTempMin(), main.getTempMax(),
                main.getHumidity(),
                wind != null ? wind.getSpeed() : 0.0,
                main.getPressure(),
                data.getVisibility() != null ? data.getVisibility() : 0
        );
    }

    private String formatForecast(ForecastData data, int days) {
        String cityName = (data.getCity() != null) ? data.getCity().getName() : "Unknown";
        String country  = (data.getCity() != null && data.getCity().getCountry() != null)
                ? ", " + data.getCity().getCountry() : "";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d-day forecast for %s%s:%n%n", days, cityName, country));

        // Take the noon-ish slot for each day (index 3 = +9 h from midnight ≈ midday)
        int slotsPerDay = 8;
        for (int day = 0; day < days; day++) {
            int slotIndex = day * slotsPerDay + Math.min(3, data.getList().size() - 1 - day * slotsPerDay);
            if (slotIndex >= data.getList().size()) break;

            ForecastData.ForecastItem item = data.getList().get(slotIndex);
            String desc = (item.getWeather() != null && !item.getWeather().isEmpty())
                    ? capitalise(item.getWeather().get(0).getDescription())
                    : "Unknown";
            String date = (item.getDtTxt() != null && item.getDtTxt().length() >= 10)
                    ? item.getDtTxt().substring(0, 10) : "N/A";

            sb.append(String.format("  %s:  %.1f°C (min %.1f°C / max %.1f°C)  —  %s%n",
                    date,
                    item.getMain().getTemp(),
                    item.getMain().getTempMin(),
                    item.getMain().getTempMax(),
                    desc));
        }
        return sb.toString();
    }

    private String capitalise(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
