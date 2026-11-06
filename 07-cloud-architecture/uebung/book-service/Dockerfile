FROM maven:3.9.11-eclipse-temurin-21-alpine as build

COPY . /src
RUN cd /src && mvn package

FROM openjdk:21-jdk-slim

COPY --from=build /src/target/book-service-*.jar /app/book-service.jar
RUN chmod +x /app/book-service.jar

ENTRYPOINT ["/app/book-service.jar"]
