# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn -B dependency:go-offline
COPY backend/src ./src
RUN mvn -B clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/ai-interview-backend.jar app.jar

ENV GEMINI_API_KEY=""
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
