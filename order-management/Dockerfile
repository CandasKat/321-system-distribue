# Multi-stage build for Spring Boot application

# Build stage
FROM gradle:8.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Environment variables that can be overridden in docker-compose
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/commandes
ENV SPRING_DATASOURCE_USERNAME=candas
ENV SPRING_DATASOURCE_PASSWORD=2003
ENV SPRING_RABBITMQ_HOST=rabbitmq
ENV SPRING_RABBITMQ_PORT=5672
ENV SPRING_RABBITMQ_USERNAME=guest
ENV SPRING_RABBITMQ_PASSWORD=guest

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]