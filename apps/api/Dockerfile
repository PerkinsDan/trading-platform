FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

COPY target/api-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]