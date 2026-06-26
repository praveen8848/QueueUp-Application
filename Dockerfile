# ==========================
# Build Stage
# ==========================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ==========================
# Runtime Stage
# ==========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy generated JAR
COPY --from=build /app/target/*.jar app.jar

# Create directory for H2 database
RUN mkdir -p /app/data

# Render exposes the PORT environment variable
EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-XX:InitialRAMPercentage=25","-jar","app.jar"]