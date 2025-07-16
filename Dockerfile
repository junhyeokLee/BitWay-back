FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/bitway.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
