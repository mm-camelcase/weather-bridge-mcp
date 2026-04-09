package com.example.weatherbridgemcp.service;

import com.example.weatherbridgemcp.exception.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WeatherServiceTest {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String API_KEY  = "test-api-key";

    private WeatherService weatherService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        weatherService = new WeatherService(restTemplate, API_KEY, BASE_URL);
    }

    // ── getCurrentWeather ──────────────────────────────────────────────────────

    @Test
    void getCurrentWeather_returnsFormattedSummary() {
        mockServer.expect(requestToUriTemplate(
                        BASE_URL + "/weather?q={city}&appid={key}&units=metric", "London", API_KEY))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(londonWeatherJson(), MediaType.APPLICATION_JSON));

        String result = weatherService.getCurrentWeather("London");

        assertThat(result).contains("London", "UK");
        assertThat(result).contains("15.0°C");
        assertThat(result).contains("Overcast clouds");
        assertThat(result).contains("80%");   // humidity
    }

    @Test
    void getCurrentWeather_cityNotFound_throwsWeatherServiceException() {
        mockServer.expect(requestToUriTemplate(
                        BASE_URL + "/weather?q={city}&appid={key}&units=metric", "Atlantis", API_KEY))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> weatherService.getCurrentWeather("Atlantis"))
                .isInstanceOf(WeatherServiceException.class)
                .hasMessageContaining("City not found");
    }

    @Test
    void getCurrentWeather_unauthorised_throwsWeatherServiceException() {
        mockServer.expect(requestToUriTemplate(
                        BASE_URL + "/weather?q={city}&appid={key}&units=metric", "London", API_KEY))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> weatherService.getCurrentWeather("London"))
                .isInstanceOf(WeatherServiceException.class)
                .hasMessageContaining("Invalid API key");
    }

    // ── getForecast ────────────────────────────────────────────────────────────

    @Test
    void getForecast_returnsFormattedForecast() {
        mockServer.expect(requestToUriTemplate(
                        BASE_URL + "/forecast?q={city}&appid={key}&units=metric&cnt={cnt}", "Paris", API_KEY, "8"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(parisForecastJson(), MediaType.APPLICATION_JSON));

        String result = weatherService.getForecast("Paris", 1);

        assertThat(result).contains("Paris", "FR");
        assertThat(result).contains("1-day forecast");
    }

    @Test
    void getForecast_invalidDays_throwsWeatherServiceException() {
        assertThatThrownBy(() -> weatherService.getForecast("London", 0))
                .isInstanceOf(WeatherServiceException.class)
                .hasMessageContaining("between 1 and 5");

        assertThatThrownBy(() -> weatherService.getForecast("London", 6))
                .isInstanceOf(WeatherServiceException.class)
                .hasMessageContaining("between 1 and 5");
    }

    // ── Test fixtures ──────────────────────────────────────────────────────────

    private String londonWeatherJson() {
        return """
                {
                  "name": "London",
                  "weather": [{"id": 804, "main": "Clouds", "description": "overcast clouds", "icon": "04d"}],
                  "main": {"temp": 15.0, "feels_like": 13.5, "temp_min": 12.0, "temp_max": 17.0, "humidity": 80, "pressure": 1012},
                  "wind": {"speed": 5.1, "deg": 270},
                  "clouds": {"all": 90},
                  "sys": {"country": "UK", "sunrise": 1680000000, "sunset": 1680045000},
                  "visibility": 9000
                }
                """;
    }

    private String parisForecastJson() {
        return """
                {
                  "list": [
                    {
                      "dt": 1680001200,
                      "main": {"temp": 18.0, "feels_like": 17.0, "temp_min": 15.0, "temp_max": 20.0, "humidity": 65},
                      "weather": [{"main": "Clear", "description": "clear sky"}],
                      "wind": {"speed": 3.2},
                      "dt_txt": "2023-03-28 09:00:00"
                    }
                  ],
                  "city": {"name": "Paris", "country": "FR"}
                }
                """;
    }
}
