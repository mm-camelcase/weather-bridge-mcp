package com.example.weatherbridgemcp;

import com.example.weatherbridgemcp.service.WeatherService;
import org.springframework.ai.tool.MethodToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Weather Bridge MCP — entry point.
 *
 * <p>Registers the two MCP tools ({@code getCurrentWeather}, {@code getForecast}) by
 * wrapping the {@link WeatherService} in a {@link ToolCallbackProvider}.  The
 * {@code spring-ai-starter-mcp-server-webmvc} auto-configuration then picks up the
 * provider and exposes the tools over the SSE endpoint at {@code /sse}.
 */
@SpringBootApplication
public class WeatherBridgeMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherBridgeMcpApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }
}
