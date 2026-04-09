# Changelog

All notable changes to this project will be documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-05-03

### Added
- Spring Boot 3.4 MCP server with WebMVC SSE transport
- `getCurrentWeather` MCP tool — current conditions for any city
- `getForecast` MCP tool — 1–5 day forecast for any city
- OpenWeatherMap API integration with proper error handling
- REST health endpoint at `GET /weather/health`
- Unit tests with `MockRestServiceServer`
- Multi-stage Docker build
- GitHub Actions CI pipeline
- Claude Desktop and Claude Code configuration examples
