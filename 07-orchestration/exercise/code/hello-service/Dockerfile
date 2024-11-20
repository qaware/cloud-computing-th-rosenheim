FROM openjdk:15

COPY target/hello-service-*.jar /app.jar
EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app.jar"]
