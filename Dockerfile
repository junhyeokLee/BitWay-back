FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean shadowJar --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/BitWay_Back-0.0.1-SNAPSHOT-all.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]