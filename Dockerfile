# ==========================
# Build Stage
# ==========================
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# ==========================
# Runtime Stage
# ==========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy generated jar
COPY --from=build /app/target/*.jar app.jar

# Create data directory for H2
RUN mkdir -p /app/data

# Render will inject PORT
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:InitialRAMPercentage=25", "-jar", "app.jar"]