package com.example.weatherbridgemcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "openweathermap.api-key=test-key",
        "openweathermap.base-url=https://api.openweathermap.org/data/2.5"
})
class WeatherBridgeMcpApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring context starts up without errors
    }
}
