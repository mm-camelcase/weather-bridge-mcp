# ── Stage 1: Build native image ────────────────────────────────────────────────
FROM ghcr.io/graalvm/graalvm-community:21 AS build
WORKDIR /app

# Install Maven 3.9 (GraalVM image is Oracle Linux based, uses microdnf)
ENV MAVEN_VERSION=3.9.9
RUN microdnf install -y curl tar findutils && \
    curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
         | tar -xzC /opt && \
    ln -s "/opt/apache-maven-${MAVEN_VERSION}/bin/mvn" /usr/local/bin/mvn

# Cache dependencies as a separate layer — only re-fetched when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress

COPY src ./src
RUN mvn -Pnative native:compile -DskipTests --no-transfer-progress

# ── Stage 2: Minimal runtime ───────────────────────────────────────────────────
# glibc (debian:12-slim) is required for the dynamically-linked native binary
FROM debian:12-slim
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app
USER appuser

COPY --from=build /app/target/weather-bridge-mcp app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget -qO- http://localhost:8080/weather/health || exit 1

ENTRYPOINT ["./app"]
