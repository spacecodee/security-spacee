# ==============================================================================
# STAGE 1: BUILDER
# Build the application using Gradle with dependency caching
# ==============================================================================
FROM gradle:8.12-jdk25 AS builder

WORKDIR /app

# Copy Gradle wrapper and config files first (better caching)
COPY gradle gradle
COPY gradlew ./
COPY gradle.properties* ./
COPY settings.gradle ./
COPY build.gradle ./

# Download dependencies separately (cached layer if build.gradle doesn't change)
RUN gradle dependencies --no-daemon --refresh-dependencies || return 0

# Copy source code
COPY src ./src

# Build the application (skip tests - they run in CI pipeline)
RUN gradle bootJar -x test --no-daemon --info

# Extract layers from the JAR (Rule 9: Layered JARs for faster deployments)
WORKDIR /app/build/libs
RUN java -Djarmode=layertools -jar ./*.jar extract

# ==============================================================================
# STAGE 2: RUNTIME
# Minimal production image with JRE only
# ==============================================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

# Metadata
LABEL maintainer="SpaceCodee Team <dev@spacecodee.com>"
LABEL version="0.0.1-SNAPSHOT"
LABEL description="Security Spacee - Enterprise RBAC Backend"
LABEL org.opencontainers.image.source="https://github.com/spacecodee/security-spacee"

# Install curl for healthcheck (optional: wget is pre-installed in alpine)
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone && \
    apk del tzdata

# Create non-root user and group (Rule 9: Security Context)
RUN addgroup -S spring && adduser -S spring -G spring

# Switch to non-root user
USER spring:spring

WORKDIR /app

# Copy extracted layers from builder stage (Order matters for optimal caching!)
# Dependencies change rarely -> cached most of the time
COPY --from=builder --chown=spring:spring /app/build/libs/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/build/libs/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/build/libs/snapshot-dependencies/ ./
# Application code changes frequently -> last layer
COPY --from=builder --chown=spring:spring /app/build/libs/application/ ./

# Health check via Spring Boot Actuator (Rule 9: Resilience)
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose application port
EXPOSE 8080

# Environment variables (can be overridden at runtime)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC" \
    TZ=UTC \
    SPRING_PROFILES_ACTIVE=prod

# Start application using layered JAR entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
