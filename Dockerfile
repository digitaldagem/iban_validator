# Stage 1: Build the application using Maven
FROM openjdk:21-ea-17-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && apt-get clean

# Copy the pom.xml
COPY pom.xml .

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using a slim JRE image
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR file from stage 1
COPY --from=build /app/target/*.jar /app/iban_validator.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "iban_validator.jar"]
