FROM maven:3.9.11-eclipse-temurin-21-alpine

COPY target/hello-service-*.jar /app.jar
EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app.jar"]
