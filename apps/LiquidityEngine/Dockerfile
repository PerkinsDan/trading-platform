FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

RUN apk add --no-cache maven

# Copy OrderProcessor
COPY ./OrderProcessor /app/OrderProcessor

# Build and install OrderProcessor
WORKDIR /app/OrderProcessor
RUN mvn clean install -DskipTests

# Copy LiquidityEngine
COPY ./LiquidityEngine /app/apps/LiquidityEngine

# Build LiquidityEngine
WORKDIR /app/apps/LiquidityEngine
RUN mvn clean package -DskipTests

# Run phase
EXPOSE 8888
CMD ["java", "-jar", "target/liquidity-engine-1.0-SNAPSHOT.jar"]
