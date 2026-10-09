package de.qaware.edu.cc.bookpublisher;

import java.time.Instant;

/**
 * An event that is published to Kafka whenever something happens to a book.
 */
public record BookEvent(String isbn, String title, String author, EventType type, Instant timestamp) {
}
