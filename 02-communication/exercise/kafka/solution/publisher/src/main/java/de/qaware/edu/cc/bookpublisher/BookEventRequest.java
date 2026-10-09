package de.qaware.edu.cc.bookpublisher;

/**
 * Request body of the REST endpoint. The timestamp is set by the publisher.
 */
public record BookEventRequest(String isbn, String title, String author, EventType type) {
}
