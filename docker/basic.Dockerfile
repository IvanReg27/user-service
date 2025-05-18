# Сборка приложения
FROM maven:3.8.4-openjdk-17-slim as builder
WORKDIR /src
COPY . .
RUN mvn clean install -DskipTests

# Финальный образ
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /src/target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "-Dserver.port=9002", "app.jar"]