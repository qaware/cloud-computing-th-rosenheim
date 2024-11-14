# Exercise: Cloud Architektur

## Task 1: Twelve Factor Apps

The [Twelve Factor Apps](https://12factor.net/) describe methods and recommendations for developing
cloud applications.

Research the given factors in your group. You can use these
[slides] (https://www.slideshare.net/Alicanakku1/12-factor-apps)
or research freely.

Prepare a short set of slides together (1 slide per factor), in which you
* state the idea behind the respective factor
* explain the recommendation

Work on the following points in your groups:

* Group 1:
  * Code base
  * Dependencies
  * Configuration
* Group 2:
  * Backing services
  * Build, release, run
  * Processes
* Group 3:
  * Port binding
  * Concurrency
  * Availability
* Group 4:
  * Dev/prod parity
  * Logs
  * Admin processes

Find a representative of your group to take over the presentation.

## Task 2: The Raft Consensus Protocol

Work out how the Raft protocol works with the help of the following
[documentation](http://thesecretlivesofdata.com/raft/).

## Task 3: etcd

See directory “etcd”.

## Task 4: Gather initial experience with Traefik and Consul

The aim of this exercise is to gain initial practical experience with Traefik and Consul.
A simple Spring Cloud REST service is set up together with Consul
for service discovery and configuration and Traeffik as an edge server.

Diese Exercise orientiert sich an diesem [Tutorial](https://m.mattmclaugh.com/traefik-and-consul-catalog-example-2c33fc1480c0).

## Preparation

* Setting up a Spring Cloud microservice is described in Exercise 1. The solution to that
  exercise serves as a starting point and will be expanded in this exercise.

## Tasks

### Test the pre-built container

First, you need to build the book service in the subdirectory `book-service`. To do this, use the command:
```shell
./mvnw package
```

Now you can build and start the container with the Docker Compose file in this directory:
```shell
docker-compose up --build
```
Test that the book service is started correctly.
After starting, the book service should be directly accessible on port 18080.
You can check this, for example, with
```
curl http://localhost:18080/api/books
```
 

### Consul Cluster (Single Node) with Docker Compose

Add a Consul service (in single-node operation) to the Docker Compose file
Use the latest official Docker image from Hashicorp: [hub.docker.com/_/consul](https://hub.docker.com/_/consul)

Make sure that the Consul UI is started and that all required ports are exposed.

<details>
<summary>Refer to the following section if you get stuck:</summary>

```
  consul:
    image: consul
    command: consul agent -server -dev -client=0.0.0.0 -ui -bootstrap -log-level warn
    ports:
      - "8400:8400"
      - "8500:8500"
      - "8600:53/udp"
```
</details>

Start the Consul service. You should now be able to access the UI by visiting [http://localhost:8500/ui/](http://localhost:8500/ui/).

### Spring Cloud Microservice

Expand the book-service so that it

* logs on to the Consul Service Discovery when starting up,
* retrieves its configuration values from Consul when starting up,
* the service interfaces (not the admin interfaces) can be accessed via Traefik

The following dependencies must be added to the pom.xml:

```xml
<!-- required for Consol discovery and configuration -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

After that, the file `bootstrap.properties` must be created under `src/main/resources`:

```properties
spring.application.name=book-service

# specify Consul host and port
# we use the CONSUL_HOST and CONSUL_PORT env variables
# later set in docker compose as well as Kubernetes
spring.cloud.consul.host=${consul.host:consul}
spring.cloud.consul.port=${consul.port:8500}

spring.cloud.consul.config.enabled=true
spring.cloud.consul.config.prefix=configuration
spring.cloud.consul.config.default-context=application

# do not fail at startup if Consul is not there
spring.cloud.consul.config.fail-fast=false

# store properties as blob in property syntax
# e.g. configuration/book-service/data
spring.cloud.consul.config.format=properties
spring.cloud.consul.config.data-key=data
```

In the same directory, you still have to expand the file `application.properties`:
```properties
# assign a unique instance ID
spring.cloud.consul.discovery.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}

# required by Docker compose and Consul to run the health check
# register IP address and heartbeats
spring.cloud.consul.discovery.prefer-ip-address=true
spring.cloud.consul.discovery.heartbeat.enabled=true
```


If you get stuck, use the following section:
```yaml
 book-service:
    build: ./book-service
    image: book-service:1.1.0
    ports:
      - 18080:18080
    depends_on:
      - consul
    networks:
      - cloud-architecture
    environment:
      - SPRING_CLOUD_CONSUL_HOST=consul
```

Finally, you have to ensure that the book-service is started after Consul.
To do this, expand the book-service docker-compose configuration with the following block:
```yaml
    depends_on:
      - consul

```

### Traefik Edge Service with Consul backend

Run the Traefik Edge Service using Docker Compose and use
Consul as the discovery backend for Traefik.

Use the official Traefik image: [https://hub.docker.com/_/traefik](https://hub.docker.com/_/traefik)
Configure the API (= Traefik Dashboard) with the CLI parameter `--api.insecure=true`.
Configure Consul as a catalog, see: [https://doc.traefik.io/traefik/providers/consul-catalog/](https://doc.traefik.io/traefik/providers/consul-catalog/)
* Expose the following ports: 80, 8080 (for the API)

Start the container with Docker Compose and pass the necessary configurations for interacting with Consul when starting.

<details>
<summary>If you get stuck, use the following section:</summary>

```yaml
  reverse-proxy:
    image: traefik
    command: --providers.consulcatalog.endpoint.address="consul:8500" --api.insecure=true
    ports:
      - 80:80
      - 8080:8080
    depends_on:
      - consul
    links:
      - consul
    networks:
      - cloud-architecture
```
</details>

### Anbinden des Book-Service an Traefik

The following properties must also be added to application.properties to correctly configure the service registration
in Consul and the tags for Traefik:

```properties
# Configuration for traefik
spring.cloud.consul.discovery.tags=traefik.enable=true,traefik.frontend.rule=PathPrefixStrip:/book-service,traefik.tags=api,traefik.frontend.entrypoint=h
```

Restart the services. If everything went well, you should now see the book service in the Traefik API.
You should now be able to call the book service via Traefik, e.g. via:
```shell
curl -H Host:book-service-uebung http://127.0.0.1/api/books
```

### Testen

The application should now be accessible at http://localhost:8081/book-service/api/books.
The Consul UI should be accessible at http://localhost:8500/ui.
The Traefik UI should be accessible at http://localhost:8080.
