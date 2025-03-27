FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

# Copy the Maven project files
COPY . /app

# Install Maven
RUN apk add --no-cache maven

# Build the application
RUN mvn clean package -DskipTests

# Copy the built JAR to the final location
RUN cp target/trading-platform-api-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
