package de.qaware.edu.cc.bookservice;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple bookshelf component to hold and manage the Book entities.
 */
@Component
public class Bookshelf {

    /**
     * Books stored by ISBN. The ISBN is the identifier of a book, so it must be unique.
     */
    private final Map<String, Book> books = new ConcurrentHashMap<>();

    /**
     * Initialize some test data.
     */
    @PostConstruct
    public void initialize() {
        create(new Book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "0345391802"));
        create(new Book("The Martian", "Andy Weir", "0553418025"));
        create(new Book("Guards! Guards!", "Terry Pratchett", "0062225758"));
        create(new Book("Alice in Wonderland", "Lewis Carroll", "3458317422"));
        create(new Book("Life, the Universe and Everything", "Douglas Adams", "0345391829"));
    }

    /**
     * Find books, all or by title.
     *
     * @param title a title to look for, may be NULL
     * @return a collection of books
     */
    public Collection<Book> findByTitle(String title) {
        if (title == null || title.isBlank()) {
            return List.copyOf(books.values());
        } else {
            return books.values()
                    .stream()
                    .filter((Book b) -> b.getTitle().equalsIgnoreCase(title))
                    .toList();
        }
    }

    /**
     * Find book by ISBN.
     *
     * @param isbn the isbn of the book
     * @return the book that matched the isbn
     * @throws BookNotFoundException if there is no book with the given ISBN
     */
    public Book findByIsbn(String isbn) {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotFoundException(isbn);
        }
        return book;
    }

    /**
     * Delete book by ISBN.
     *
     * @param isbn isbn of the book
     */
    public void delete(String isbn) {
        books.remove(isbn);
    }

    /**
     * Create book if no book with the same ISBN is present.
     *
     * @param book the book to create
     * @return true if created, otherwise false
     */
    public boolean create(Book book) {
        return books.putIfAbsent(book.getIsbn(), book) == null;
    }

    /**
     * Find and update the book with given ISBN. The ISBN itself can not be changed.
     *
     * @param isbn the ISBN to update
     * @param book the updated book
     * @return the updated book
     * @throws BookNotFoundException if there is no book with the given ISBN
     */
    public Book update(String isbn, Book book) {
        Book updated = books.computeIfPresent(isbn, (key, existing) -> new Book(book.getTitle(), book.getAuthor(), isbn));
        if (updated == null) {
            throw new BookNotFoundException(isbn);
        }
        return updated;
    }
}
