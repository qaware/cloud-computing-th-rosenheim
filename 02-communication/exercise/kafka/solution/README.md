# Solution: Messaging with Kafka

The solution consists of two independent Spring Boot applications:

* [publisher](publisher): REST endpoint `POST /api/book-events` on port 8080, publishes to the topic `book-events`
* [consumer](consumer): listens to `book-events`, exposes the inventory via `GET /api/inventory` on port 8081

## Run

```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:4.3.1

cd publisher && ./mvnw spring-boot:run
cd consumer && ./mvnw spring-boot:run
```

```bash
curl -i -X POST localhost:8080/api/book-events -H 'Content-Type: application/json' \
     -d '{"isbn":"0553418025","title":"The Martian","author":"Andy Weir","type":"ADDED"}'
curl localhost:8081/api/inventory
```
