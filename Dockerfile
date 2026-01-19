# ==============================================================================
# STAGE 1: BUILDER
# Build the application using Gradle with dependency caching
# ==============================================================================
# Using official Gradle image with JDK 25 (Alpine for smaller size)
FROM gradle:9.3.0-jdk25-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and config files first (better caching)
COPY gradle gradle
COPY gradlew ./
COPY gradle.properties* ./
COPY settings.gradle ./
COPY build.gradle ./

# Download dependencies separately (cached layer if build.gradle doesn't change)
RUN gradle dependencies --no-daemon --refresh-dependencies || true

# Copy source code
COPY src ./src

# Build the application and extract layers in a single step
# (Skip tests - they run in CI pipeline)
# (Rule 9: Layered JARs for faster deployments)
RUN gradle bootJar -x test --no-daemon && \
    cd build/libs && \
    java -Djarmode=layertools -jar ./*.jar extract

# ==============================================================================
# STAGE 2: RUNTIME
# Minimal production image with JRE only
# ==============================================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

# Metadata (OCI Image Spec)
LABEL maintainer="SpaceCodee Team <dev@spacecodee.com>" \
      version="0.0.1-SNAPSHOT" \
      description="Security Spacee - Enterprise RBAC Backend" \
      org.opencontainers.image.source="https://github.com/spacecodee/security-spacee"

# Install curl, configure timezone, and create non-root user in a single layer
# (SonarQube docker:S7031 - Merge consecutive RUN instructions)
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone && \
    apk del tzdata && \
    addgroup -S spring && \
    adduser -S spring -G spring

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
# Java 25: --enable-native-access=ALL-UNNAMED for Netty native libraries
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC --enable-native-access=ALL-UNNAMED" \
    TZ=UTC \
    SPRING_PROFILES_ACTIVE=prod

# Start application using layered JAR entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
