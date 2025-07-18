FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean shadowJar

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/BitWay_Back-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]