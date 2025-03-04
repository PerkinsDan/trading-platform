FROM eclipse-temurin:23-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file from Maven or Gradle build
COPY target/api-1.0-SNAPSHOT.jar /app/app.jar

# Expose the HTTP server port
EXPOSE 8080

# Run the Vert.x application
CMD ["java", "-jar", "/app/app.jar"]