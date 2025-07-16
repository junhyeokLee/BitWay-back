# 1단계: JAR 빌드
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar
RUN ls -alh build/libs
RUN find /app -name "*.jar"

# 2단계: JAR 실행 환경 설정
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/bitway.jar ./app.jar

# JAR 존재 확인 (Render 로그에서 확인용)
RUN ls -alh /app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
