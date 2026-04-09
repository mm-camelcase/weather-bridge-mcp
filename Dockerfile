# ── Stage 1: Build ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies as a separate layer — only re-fetched when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress

COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/target/weather-bridge-mcp-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD wget -qO- http://localhost:8080/weather/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
