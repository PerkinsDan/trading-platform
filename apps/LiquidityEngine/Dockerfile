FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy the entire project
COPY . /app/apps/LiquidityEngine

# Build the trading-platform-api module
WORKDIR /app/apps/LiquidityEngine
RUN mvn clean package -DskipTests

# Copy the built JAR to the final location
RUN cp target/LiquidityEngine-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8888

CMD ["java", "-jar", "/app/app.jar"]