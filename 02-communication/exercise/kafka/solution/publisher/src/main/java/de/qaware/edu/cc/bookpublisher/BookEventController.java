package de.qaware.edu.cc.bookpublisher;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST entrypoint to trigger book events.
 */
@RestController
@RequestMapping(value = "/api/book-events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class BookEventController {

    private final BookEventPublisher publisher;

    public BookEventController(BookEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publish(@RequestBody BookEventRequest request) {
        publisher.publish(new BookEvent(request.isbn(), request.title(), request.author(), request.type(), Instant.now()));
    }
}
