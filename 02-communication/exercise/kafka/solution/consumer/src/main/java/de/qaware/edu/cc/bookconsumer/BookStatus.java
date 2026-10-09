package de.qaware.edu.cc.bookconsumer;

/**
 * Current state of a book in the library, derived from the consumed events.
 */
public record BookStatus(String isbn, String title, String author, boolean available) {
}
