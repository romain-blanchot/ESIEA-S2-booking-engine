# syntax=docker.io/docker/dockerfile:1

# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy maven wrapper and pom
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -DskipTests

# Copy source code
COPY src/ src/

# Build the application
RUN ./mvnw package -DskipTests -Dspring-boot.build-image.skip=true

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Create non-root user
RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 --ingroup spring spring

# Copy the jar from builder
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
