package de.qaware.edu.cc.bookservice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

/**
 * The REST resource for the books.
 */
@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private final Bookshelf bookshelf;

    public BookController(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }

    @GetMapping
    @Operation(summary = "Find books", description = "Finds books by a given title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all books", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Book.class))
                    )
            })
    })
    public Collection<Book> books(@Parameter(description = "title to search")
                           @RequestParam(value = "title", required = false, defaultValue = "") String title) {
        return bookshelf.findByTitle(title);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the book, the Location header contains its URL", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)
                    )
            }),
            @ApiResponse(responseCode = "409", description = "Book with this ISBN already exists", content = @Content)
    })
    public ResponseEntity<Book> create(@RequestBody Book book) {
        boolean created = bookshelf.create(book);
        if (created) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{isbn}")
                    .buildAndExpand(book.getIsbn())
                    .toUri();
            return ResponseEntity.created(location).body(book);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{isbn}")
    @Operation(summary = "Find book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)
                    )
            }),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public Book byIsbn( @Parameter(description = "ISBN to search", required = true)
                            @PathVariable("isbn") String isbn) {
        return bookshelf.findByIsbn(isbn);
    }

    @PutMapping(value = "/{isbn}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update book by ISBN", description = "Updates title and author. The ISBN can not be changed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the book", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)
                    )
            }),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    public Book update(@Parameter(description = "ISBN of the book to update", required = true)
                           @PathVariable("isbn") String isbn, @RequestBody Book book) {
        return bookshelf.update(isbn, book);
    }

    @DeleteMapping("/{isbn}")
    @Operation(summary = "Delete book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted (or did not exist)")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ISBN to delete", required = true)
                           @PathVariable("isbn") String isbn) {
        bookshelf.delete(isbn);
        return ResponseEntity.noContent().build();
    }
}
