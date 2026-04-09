# Contributing

## Prerequisites

- Java 21+
- Maven 3.9+
- An OpenWeatherMap API key ([free tier](https://openweathermap.org/api) is sufficient)

## Local setup

```bash
git clone https://github.com/mm-camelcase/weather-bridge-mcp.git
cd weather-bridge-mcp
cp .env.example .env
# Edit .env and add your OPENWEATHERMAP_API_KEY
```

## Running locally

```bash
# Pick up the API key from the environment
export $(cat .env | xargs)

mvn spring-boot:run
```

The server starts on `http://localhost:8080`. Verify with:

```bash
curl http://localhost:8080/weather/health
curl "http://localhost:8080/weather/current/London"
curl "http://localhost:8080/weather/forecast/Paris?days=3"
```

## Running tests

```bash
mvn test
```

Tests use `MockRestServiceServer` — no real API calls are made, so no API key is needed.

## Docker

```bash
docker-compose up --build
```

## Code style

- 4-space indentation for Java, 2-space for XML/YAML
- `.editorconfig` enforces consistent line endings and whitespace

## Opening a pull request

1. Fork the repo and create a feature branch: `git checkout -b feature/my-thing`
2. Run `mvn verify` to confirm all tests pass
3. Open a PR against `main` with a clear description of the change
