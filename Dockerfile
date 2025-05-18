# Этап сборки
FROM maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /usr/src/app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Этап выполнения
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /usr/src/app/target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]