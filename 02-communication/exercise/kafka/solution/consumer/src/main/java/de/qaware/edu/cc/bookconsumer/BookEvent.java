package de.qaware.edu.cc.bookconsumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

/**
 * The consumer's own view of a book event. It is intentionally not shared with the publisher:
 * both services only agree on the JSON format of the message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookEvent(String isbn, String title, String author, EventType type, Instant timestamp) {
}
