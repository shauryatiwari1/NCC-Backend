# -------- Stage 1: Build with Maven --------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy everything and build the jar
COPY . .
RUN mvn clean package -DskipTests

# -------- Stage 2: Run with slim JDK --------
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose app port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
