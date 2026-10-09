# Exercise: Messaging with Kafka

The goal of this exercise is to connect two microservices asynchronously using Apache Kafka.
The library from the REST exercise now publishes events whenever something happens to a book
(it is added, borrowed, returned or removed).

* The **publisher** is a Spring Boot service with a small REST endpoint. Every request is turned into a `BookEvent`
  and sent to the Kafka topic `book-events`.
* The **consumer** is a second, independent Spring Boot service. It listens to the topic and builds an inventory
  that shows which books are currently available.

```
             HTTP POST                 Kafka topic                  HTTP GET
  curl  ───────────────▶ publisher ──▶ book-events ──▶ consumer ◀─────────────── curl
                         (port 8080)   (3 partitions)  (port 8081)
```

The two services never call each other directly and do not share any code. They only agree on the topic name
and on the JSON format of the messages.

## Setup

### Start Kafka

Start a single Kafka broker with Docker (KRaft mode, no ZooKeeper required):

```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:4.3.1
```

The broker is now available under `localhost:9092`. When you are done, stop and remove it with `docker rm -f kafka`.

### Create the projects

Create **two** projects with the Spring Boot Initializr (https://start.spring.io): one named `book-publisher` and one
named `book-consumer`. For both projects select Java, Maven, Spring Boot 4.x and Java 21 (or newer), and add the
following dependencies:

* Spring Web
* Spring for Apache Kafka

Generate the projects, unzip them into your workspace and open them in your IDE.

## Tasks

### Task 1: Get to know Kafka on the command line

The Kafka image ships with command line tools under `/opt/kafka/bin`. Use them to get a feeling for topics,
producers and consumers before writing any code.

(1) Create a topic `hello` with 3 partitions and inspect it:

```bash
docker exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic hello --partitions 3
docker exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic hello
```

(2) Open a second terminal and start a console consumer:

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello
```

(3) In the first terminal, start a console producer and type a few messages (one per line, exit with `Ctrl+C`):

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic hello
```

(4) Stop the consumer and start it again, this time with the option `--from-beginning`. What do you observe?
How is this different from a classic message queue?

### Task 2: Write the publisher

(1) Create the message. Use Java records for the event and an enum for the type of event:

```java
public enum EventType {
    ADDED, BORROWED, RETURNED, REMOVED
}
```

```java
public record BookEvent(String isbn, String title, String author, EventType type, Instant timestamp) {
}
```

(2) Configure the connection to Kafka in `src/main/resources/application.properties`. The key of a message is a plain
string, the value is serialized as JSON:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer
# Do not send Java class names as headers - the consumer has its own BookEvent class
spring.kafka.producer.properties.spring.json.add.type.headers=false
```

(3) Let Spring create the topic `book-events` with 3 partitions on startup. Declare a bean of type `NewTopic`
in a `@Configuration` class (hint: `TopicBuilder`).

(4) Implement a `BookEventPublisher` component that sends events to the topic using a `KafkaTemplate`:

```java
@Component
public class BookEventPublisher {

    private final KafkaTemplate<String, BookEvent> kafkaTemplate;

    public BookEventPublisher(KafkaTemplate<String, BookEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(BookEvent event) {
        // send the event to the topic "book-events"
    }
}
```

Use the ISBN as the **message key**. Why is this important?
(hint: think about which partition a message goes to and what that means for the order of the events of one book)

`KafkaTemplate.send(...)` works asynchronously and returns a `CompletableFuture`. Log the partition and offset of every
message once it was sent successfully, and log an error if sending failed.

(5) Add a `@RestController` that accepts a book event via `POST /api/book-events` and publishes it.
The timestamp is set by the publisher. As the request is only handed over to Kafka and not processed yet,
the endpoint should return HTTP 202 (Accepted).

(6) Start the publisher (`./mvnw spring-boot:run`) and publish some events:

```bash
curl -i -X POST localhost:8080/api/book-events -H 'Content-Type: application/json' \
     -d '{"isbn":"0345391802","title":"The Hitchhiker'"'"'s Guide to the Galaxy","author":"Douglas Adams","type":"ADDED"}'
```

(On Windows, use Git Bash, WSL or the HTTP client of your IDE.)

Check with the console consumer from Task 1 that the events arrive in the topic `book-events`. Add the option
`--formatter-property print.key=true` to also see the message keys.

### Task 3: Write the consumer

(1) Configure the consumer in `src/main/resources/application.properties`. It should run on port 8081, so that it
does not collide with the publisher:

```properties
server.port=8081

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=book-consumer
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JacksonJsonDeserializer
# The publisher sends no type headers, so tell the deserializer which class to create
spring.kafka.consumer.properties.spring.json.value.default.type=<your package>.BookEvent
```

What does `auto-offset-reset=earliest` mean?

(2) Create the consumer's own `BookEvent` and `EventType`. Do **not** add a dependency on the publisher project.
Only the JSON format is shared between the services.

(3) Implement a listener that receives the events. Log the event together with its partition and offset:

```java
@Component
public class BookEventListener {

    @KafkaListener(topics = "book-events")
    public void onBookEvent(ConsumerRecord<String, BookEvent> record) {
        // log and process the event
    }
}
```

(4) Build an in-memory inventory from the events (e.g. a `ConcurrentHashMap` keyed by ISBN):

* `ADDED` and `RETURNED` mark a book as available
* `BORROWED` marks a book as not available
* `REMOVED` removes the book from the inventory

Expose the inventory via `GET /api/inventory` (e.g. a list of `BookStatus(isbn, title, author, available)`).

(5) Start the consumer, publish some events with the publisher and check the inventory:

```bash
curl localhost:8081/api/inventory
```

### Task 4: Experiments

Try the following and explain what you observe:

1. **Loose coupling:** Stop the consumer. Publish a few events. Start the consumer again. Are any events lost?
2. **Replay:** Stop the consumer and delete its committed offsets:
   ```bash
   docker exec kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group book-consumer --reset-offsets --to-earliest --topic book-events --execute
   ```
   Start the consumer again. What happens to the inventory? Why is this useful?
3. **Scaling:** Start a second instance of the consumer on a different port
   (`./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8082`). Look at the log messages
   `partitions assigned: ...` of both instances. Publish events for different books. Which instance gets which events?
   What happens if you start a fourth instance while the topic only has 3 partitions?
   Inspect the consumer group:
   ```bash
   docker exec kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group book-consumer
   ```
4. **Publish-subscribe:** Start a consumer instance with a different group id
   (`--spring.kafka.consumer.group-id=book-statistics`). Which events does it receive?
5. Both consumer instances in experiment 3 keep their own in-memory inventory. Query both of them. What is the problem
   and how could it be solved?

## Sources

Kafka
* https://kafka.apache.org/documentation/#gettingStarted
* https://hub.docker.com/r/apache/kafka

Spring for Apache Kafka
* https://docs.spring.io/spring-boot/reference/messaging/kafka.html
* https://docs.spring.io/spring-kafka/reference/
