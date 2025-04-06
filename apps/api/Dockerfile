FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy the entire project
COPY . /app/apps

# Build and install the order-processor module
WORKDIR /app/apps/OrderProcessor
RUN mvn clean install -DskipTests

# Build the trading-platform-api module
WORKDIR /app/apps/api
RUN mvn clean package -DskipTests

# Copy the built JAR to the final location
RUN cp target/trading-platform-api-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]