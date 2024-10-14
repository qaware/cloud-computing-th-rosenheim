package de.qaware.edu.cc.bookservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

/**
 * The REST resource for the books.
 */
@RestController
@RequestMapping("/api/books")
@OpenAPIDefinition
public class BookController {

    @Autowired
    private Bookshelf bookshelf;

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

    @PostMapping
    @Operation(summary = "Create book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the book"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    public ResponseEntity<URI> create(@RequestBody Book book) {
        boolean created = bookshelf.create(book);
        if (created) {
            return new ResponseEntity<>(URI.create("/api/books/" + book.getIsbn()), HttpStatus.OK);
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
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public Book byIsbn( @Parameter(description = "ISBN to search", required = true)
                            @PathVariable("isbn") String isbn) {
        return bookshelf.findByIsbn(isbn);
    }

    @PutMapping("/{isbn}")
    @Operation(summary = "Update book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the book", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)
                    )
            })
    })
    public Book update(@Parameter(description = "ISBN to search", required = true)
                           @PathVariable("isbn") String isbn, @RequestBody Book book) {
        return bookshelf.update(isbn, book);
    }

    @DeleteMapping("/{isbn}")
    @Operation(summary = "Delete book by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted")
    })
    public ResponseEntity<?> delete(@Parameter(description = "ISBN to delete", required = true)
                           @PathVariable("isbn") String isbn) {
        bookshelf.delete(isbn);
        return ResponseEntity.noContent().build();
    }
}
