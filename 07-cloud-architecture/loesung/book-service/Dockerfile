FROM maven:3.9.11-eclipse-temurin-21-alpine

COPY target/book-service-1.1.0.jar /app/
RUN chmod +x /app/book-service-1.1.0.jar

ENTRYPOINT ["/app/book-service-1.1.0.jar"]
