FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy the entire project
COPY . /app/apps/MarketData

WORKDIR /app/apps/MarketData
RUN mvn clean install -DskipTests

# Copy the built JAR to the final location
RUN cp target/market-data-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 12000

CMD ["java", "-jar", "/app/app.jar"]