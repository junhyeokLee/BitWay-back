FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar

# jar 파일 위치 디버깅 로그 추가
RUN echo "=== CHECK jar in builder ===" && find /app -name "*.jar" && ls -alh /app/build/libs

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# jar 존재 여부 디버깅
COPY --from=builder /app/build/libs/bitway.jar ./app.jar

# 확인 로그
RUN echo "=== CHECK app.jar in final ===" && ls -alh /app && find /app -name "*.jar"

EXPOSE 8080
CMD java -jar /app/app.jar
