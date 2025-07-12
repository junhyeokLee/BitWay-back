FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN ./gradlew clean shadowJar

EXPOSE 8080

CMD ["java", "-jar", "build/libs/bitway.jar"]