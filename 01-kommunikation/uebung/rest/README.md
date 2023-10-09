# Übung: Das REST Protokoll

Ziel dieser Übung ist es ein einfaches REST API für eine Bibliothek zu erstellen. Das API soll einfache Abfragen,
sowie ein CRUD Interface zum Anlegen, Aktualisieren und Löschen von Büchern ermöglichen.

## Vorbereitung

1. Zunächst muss ein Anwendungsrumpf für den Microservice und das REST API erstellt werden. Wir verwenden hierfür
den Spring Boot Initializr. Rufen sie hierfür die folgende URL auf: https://start.spring.io

2. Passen sie die Projekt Metadaten nach ihren Bedürfnissen an. Wählen sie Java als Sprache und Maven als Build-Tool.

3. Fügen sie die folgenden Dependencies hinzu:
  * Spring Web

4. Generieren (`mvnw idea:idea` oder `eclipse:eclipse`) und laden sie das Projekt und speichern sie es in ihrem Arbeitsbereich.

5. Öffnen sie eine Console, gehen sie in das Projektverzeichnis und führen sie folgendes Kommand aus: `mvnw install`


## Aufgaben

### Aufgabe 1: REST-API mit Spring MVC erstellen

Bei dieser Aufgabe geht es darum, eine einfache REST-Schnittstelle aufzubauen. Wir verwenden hierfür Spring MVC mit Spring Boot. Ein Getting Started finden sie hier: https://spring.io/guides/gs/spring-boot/

(1) Initiale Anwendungslogik erstellen:

(1.1) Entwerfen sie zunächst eine einfache Datenklasse um Bücher zu repräsentieren. Die Klasse soll mindestens die Felder `title`, `isbn` und `author` enthalten. Zusätzlich soll die Klasse beim Deserialisieren unbekannte
JSON Felder ignorieren.

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    private String title;
    private String author;
    private String isbn;

    // getter and setter
}
```

(1.2) Implementieren sie eine Bücherverwaltung, die einige Beispielbücher anlegt und erlaubt die Bücher abzufragen:

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

Zusätzlich muss noch eine Fehlermeldung definiert werden: 
```java
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found.");
    }
}
```

(2) Fügen sie nun eine REST Controller Klasse hinzu. Diese dient als Haupteinstiegspunkt für das Book API. Das API soll unter dem Pfad `/api/books` erreichbar sein und als Media-Type `application/json` produzieren.

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
  // implement methods
}
```

(3) REST Interface erstellen:

(3.1) Fügen sie der REST Resource nun entsprechende Methoden zum Abruf von Büchern hinzu. Es soll die Möglichkeit geben alle Bücher per `GET /api/books` abzurufen sowie einzelne Bücher mittels ISBN per `GET /api/books/{isbn}`. Implementieren sie die Business-Logik rudimentär (statische Liste statt DB). Achten sie bei
der Implementierung auf die Verwendung der korrekten HTTP Verben und Status-Codes, z.B. für den Fall das ein Buch per ISBN nicht gefunden wurde.

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

(3.2) Erstellen sie einen Exceptionmapper, damit Java-Exceptions in HTTP-Statuscodes umgewandelt werden:
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

(4) Starten Sie die Anwendung mit dem Befehl `mvnw spring-boot:run` (alternativ können sie die Anwendung natürlich aus der IDE heraus starten). 
Die Anwendung und das REST API sollte nun unter der folgenden URL erreichbar sein: `http://localhost:8080/api/books`.

Testen sie manuell die beiden erstellten Endpunkte mit dem Browser. Probieren sie den Fehler aus dem ExceptionMapper zu provozieren.

### Aufgabe 2: API Dokumentation

Eine gute REST API braucht Dokumentation bzw. eine Beschreibung der angebotenen
Funktionalität die von Maschinen verarbeitet werden kann. Der Standard hierfür ist OpenAPI (ehemals Swagger).

(1) Fügen sie zunächst in der `pom.xml` die folgenden Dependencies hinzu:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

(2) Annotieren und dokumentieren sie nun die vorhandenen Klassen der REST-API über OpenAPI-Annotationen zusätzlich zu den bereits vorhandenen Spring-Annotationen. Nutzen Sie hierfür die folgenden OpenAPI-Annotationen, eine Beschreibung der Annotationen ist hier zugänglich: https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations.

| Swagger-Annotation                 | Code-Element                                                                                                                                    |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `@OpenAPIDefinition`               | Metadaten für die gesamte OpenAPI Definition                                                                               |
| `@Operation`                       | Einzelne Methode der Ressourcen-Klasse                                                                                                          |
| `@ApiResponses` und `@ApiResponse` | Antworten einer Methode der Ressourcen-Klasse (überlegen Sie sich hier mögliche Fehlersituationen und bilden Sie diese auf http-Status-Codes ab) |
| `@Schema`                          | Entitäts-Klasse und Properties der Entität                                                                                                      |

(3) Starten Sie die Anwendung nun neu. Die API-Beschreibung sollte nun unter der URL http://localhost:8080/v3/api-docs zugänglich sein.

(4) Die API-Beschreibung ist nicht nur als JSON verfügbar, sondern kann auch mit der Swagger UI exploriert werden. Öffnen Sie die Swagger UI unter http://localhost:8080/swagger-ui/index.html.


### Kür: REST-API weiter ausbauen
Bauen Sie die REST-Schnittstelle weiter aus und fügen sie Logik zum Anlegen, Aktualisieren und Löschen von Büchern hinzu:

* `DELETE /api/books/{isbn}` löscht ein Buch und gibt bei Erfolg HTTP 204 zurück
* `POST /api/books` legt ein Buch an, akzeptiert `application/json` und gibt HTTP 201 mit der neuen URL zurück.
* `PUT /api/books/{isbn}` aktualisiert das Buch, akzeptiert `application/json` und gibt HTTP 200 zurück.

Überlegen Sie sich weitere Anwendungsfälle, die die Schnittstelle abbilden soll als einfache Liste (auf Papier). Leiten Sie aus den Anwendungsfällen ein Datenmodell ab (auf Papier). Erstellen Sie aus den Anwendungsfällen, dem Datenmodell und den vorgestellten Entwurfsregeln eine REST-Schnittstelle.


## Quellen
Diese Übung soll auch eine eigenständige Problemlösung auf Basis von Informationen aus dem Internet vermitteln. Sie können dazu für die eingesetzten Technologien z.B. die folgenden Quellen nutzen:

Maven
* http://maven.apache.org/guides/getting-started

Spring Boot
* https://start.spring.io
* https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
* https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html


OpenAPI/Swagger
* http://swagger.io
* https://github.com/swagger-api/swagger-core
