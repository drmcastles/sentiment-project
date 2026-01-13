# Stage 1: Сборка
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Запуск 
# Используем легкий образ (~40MB)
FROM bellsoft/liberica-runtime-container:jre-17-musl

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
