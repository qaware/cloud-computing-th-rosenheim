# Exercise: REST APIs

The goal of this is exercise is to implement a REST interface for a library. It should feature basic create, update and delete 
functionality. 

## Setup

1. First, an application skeleton for the microservice and the REST API must be created.
For this, we use the Spring Boot Initializr. Open the following URL: https://start.spring.io

2. Adjust the project metadata according to your needs. Select Java as the language and Maven as the build tool.

3. Add the following dependencies:
  Spring Web

4. Generate (`mvnw idea:idea` or `eclipse:eclipse`) and load the project, then save it in your workspace.

5. Open a console, navigate to the project directory, and execute the following command: `mvnw install`


## Tasks

### Task 1: Create a REST-API with Spring MVC

This task is about building a simple REST interface. For this, we will use Spring MVC with Spring Boot. You can find a getting started guide here: https://spring.io/guides/gs/spring-boot/.

(1) Create the initial application logic:

(1.1) First, design a simple data class to represent books. 
The class should contain at least the fields `title`, `isbn`, and `author`. Additionally, the class should ignore unknown JSON fields during deserialization.

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    private String title;
    private String author;
    private String isbn;

    // getter and setter
}
```

(1.2) Implement a book management system that creates some example books and allows querying the books:

```java
@Component
public class Bookshelf {

    private final Set<Book> books = new HashSet<>();

    @PostConstruct
    public void initialize() {
        books.add(new Book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "0345391802"));
        books.add(new Book("The Martian", "Andy Weir", "0553418025"));
        books.add(new Book("Guards! Guards!", "Terry Pratchett", "0062225758"));
        books.add(new Book("Alice in Wonderland", "Lewis Carroll", "3458317422"));
        books.add(new Book("Life, the Universe and Everything", "Douglas Adams", "0345391829"));
    }

    public Collection<Book> findByTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            return books;
        } else {
            return books
                    .stream()
                    .filter((Book b) -> b.getTitle().equalsIgnoreCase(title))
                    .collect(Collectors.toList());
        }
    }

    public Book findByIsbn(String isbn) {
        return books
                .stream()
                .filter((Book b) -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }
}
```

Additionally, you need to define an error message: 

```java
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found.");
    }
}
```

(2) Now add a class that is annotated as RestController. This class marks the entrypoint to the Book API. The API shall be available under the path `/api/books` and produce content with media-type `application/json`.

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
  // implement methods
}
```

(3) Implement REST Interface :

(3.1) Add the appropriate methods to the REST resource for retrieving books. 
It should be possible to retrieve all books via `GET /api/books` as well as individual books by ISBN via `GET /api/books/{isbn}`. 
Implement the business logic in a basic manner (using a static list instead of a database). 
Make sure to use the correct HTTP verbs and status codes, for example, in the case where a book is not found by ISBN.

```java
    @GetMapping
    public Collection<Book> books(
            @RequestParam(value = "title", required = false, defaultValue = "") String title) {
        return bookshelf.findByTitle(title);
    }

    @GetMapping("/{isbn}")
    public Book byIsbn(@PathVariable("isbn") String isbn) {
        return bookshelf.findByIsbn(isbn);
    }
```

(3.2) Create an exception mapper that converts java exceptions to http status codes:
```java
@ControllerAdvice
class BookNotFoundExceptionMapper {

    @ResponseBody
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String bookNotFoundHandler(BookNotFoundException ex) {
        return ex.getMessage();
    }
}
```

(4) Start the application with the command `mvnw spring-boot:run` or using the IDE.
The application and its API should be available under: `http://localhost:8080/api/books`.

Manually test the created endpoints in the browser. Try to produce an error the triggers the exception mapper.

### Task 2: API documentation

A good REST API needs documentation or a description of the offered functionality that can be processed by machines. The standard for this is OpenAPI (formerly Swagger).

(1) Add the following dependency to the `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

(2) Now annotate and document the existing classes of the REST API with OpenAPI annotations in addition to the already existing Spring annotations. 
Use the following OpenAPI annotations. A description of the annotations can be accessed here: https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations.

| Swagger-Annotation                 | Code-Element                                                                                                            |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `@OpenAPIDefinition`               | Metadata for the whole OpenAPI Definition                                                                               |
| `@Operation`                       | Single method of the resource class                                                                                     |
| `@ApiResponses` und `@ApiResponse` | Response of a method in the resource class (think about potential error situations and model them in the api defintion) |
| `@Schema`                          | Model classes that are relevant for serialization/deserialization.                                                      |

(3) Restart the application. The API description should now be available under http://localhost:8080/v3/api-docs.

(4) The API description is not available as JSON only, but can be explored visually using a web UI. Open the Swagger UI under http://localhost:8080/swagger-ui/index.html.


### Task 3: Extend the REST-API
Extend the read only Rest API by offering endpoints for creating, updating and deleting books. 

* `DELETE /api/books/{isbn}` deletes a book and returns HTTP 204 when successful
* `POST /api/books`  creates a book, accepts `application/json` and returns HTTP 201 including the new URL of the created book.
* `PUT /api/books/{isbn}` updates a book, accepts `application/json` and returns HTTP 200 on success.

Think of additional use cases that the interface should represent as a simple list (on paper). 
Derive a data model from the use cases (on paper). 
Create a REST interface based on the use cases, the data model, and the design rules presented.

## Quellen
This exercise is also intended to teach independent problem-solving based on information from the internet. For the technologies used, you can utilize the following sources, for example:

Maven
* http://maven.apache.org/guides/getting-started

Spring Boot
* https://start.spring.io
* https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
* https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html


OpenAPI/Swagger
* http://swagger.io
* https://github.com/swagger-api/swagger-core
