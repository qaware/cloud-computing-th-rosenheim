package de.qaware.edu.cc.bookservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {
    private final Bookshelf bookshelf;

    @Autowired
    public BookController(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }

    @GetMapping
    @Operation(summary = "Find books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all books", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Book.class))))
    })
    public Collection<Book> books(@Parameter(description = "title to search") @RequestParam(value = "title", required = false) String title) {
        return bookshelf.findByTitle(title);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the book"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    public ResponseEntity<Void> create(@RequestBody Book book) {
        boolean created = bookshelf.create(book);
        if (created) {
            return ResponseEntity.created(URI.create("/api/books/" + book.getIsbn())).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping(value = "/{isbn}")
    @Operation(summary = "Find book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book", content = @Content(schema =  @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public Book byIsbn(@Parameter(description = "ISBN to search") @PathVariable("isbn") String isbn) {
        return bookshelf.findByIsbn(isbn);
    }

    @PutMapping(value = "/{isbn}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Updated the book")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @Parameter(description = "ISBN to search") @PathVariable("isbn") String isbn,
            @RequestBody Book book
    ) {
        bookshelf.update(isbn, book);
    }

    @DeleteMapping("/{isbn}")
    @Operation(summary = "Delete book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "ISBN to delete") @PathVariable("isbn") String isbn) {
        bookshelf.delete(isbn);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<?> handleBookNotFoundException(BookNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
