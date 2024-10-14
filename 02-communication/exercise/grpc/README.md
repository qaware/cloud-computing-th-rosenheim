# Exercise: gRPC

The goal of this exercise is to create a simple gRPC service for a library. 
The API should allow for simple queries as well as a CRUD interface to create, update, and delete books.

The following operations should be possible (given here as an example in Java syntax):

* `Collection<Book> listAll()`
* `void add(Book book)`
* `void delete(String isbn)`
* `void update(String isbn, Book newBook)` 

A book consists of the attributes `String isbn`, `String title` and `String author`. `isbn` is the ID of a book.

## Setup

Familiarize yourself with gRPC. Read the [gRPC Tutorial](https://grpc.io/docs/languages/java/basics/).
Then, you should create a new project that includes both the server and the client. Whether you use Maven or Gradle is up to you. 
The [solution](solution) is implemented with Maven.

gRPC services are generated from `.proto` files. There are plugins for Gradle and Maven that handle this. 
These are shown on the [gRPC GitHub page](https://github.com/grpc/grpc-java).

## Define gRPC Service

Now define your gRPC `BookService` in a `.proto` file. In doing so, you need to design the API of the service (see above).
This API also includes input and return types, which are defined in the [Protocol Buffers format](https://developers.google.com/protocol-buffers/docs/proto3).

## Create the server

Implement the gRPC Server. Start the server on a port of your choice in the `main`-method.

## Create the client

To test the server, you need a grpc client. Let's implement one. Create a new `main`-methode for the client. 
Implement the client by using the generated client classes to communicate with your server. 

# Quellen

* https://github.com/grpc/grpc-java
* https://grpc.io/docs/languages/java/basics/
* https://www.baeldung.com/grpc-introduction
* https://developers.google.com/protocol-buffers/docs/proto3
