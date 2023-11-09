FROM maven:3.5-jdk-8-alpine as build

COPY . /src
RUN cd /src && mvn package

FROM openjdk:8u151-jre-slim

COPY --from=build /src/target/book-service-*.jar /app/book-service.jar
RUN chmod +x /app/book-service.jar

ENTRYPOINT ["/app/book-service.jar"]
