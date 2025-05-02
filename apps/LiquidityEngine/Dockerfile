# Use an official Maven image to build the Java projects
FROM eclipse-temurin:23-jdk-alpine

RUN apk add --no-cache maven

# Create working directory
WORKDIR /build

# Copy both projects into the image
COPY ../OrderProcessor /build/OrderProcessor
COPY . /build/LiquidityEngine

# First, build and install OrderProcessor into the local Maven repo
WORKDIR /build/OrderProcessor
RUN mvn clean install -DskipTests

# Now build LiquidityEngine (which can now use OrderProcessor as a dependency)
WORKDIR /build/LiquidityEngine
RUN mvn clean package -DskipTests

# Set working directory
WORKDIR /app

# Copy the JAR from the build stage
COPY  /build/LiquidityEngine/target/LiquidityEngine-*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
