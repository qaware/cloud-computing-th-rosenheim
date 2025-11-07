FROM openjdk:21-jdk-slim

COPY target/book-service-1.1.0.jar /app/
RUN chmod +x /app/book-service-1.1.0.jar

ENTRYPOINT ["/app/book-service-1.1.0.jar"]
