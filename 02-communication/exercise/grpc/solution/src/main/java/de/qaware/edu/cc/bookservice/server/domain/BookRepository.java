package de.qaware.edu.cc.bookservice.server.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores books.
 * <p>
 * Reads are lock-free, mutations are synchronized so that {@link #update} is atomic.
 */
public class BookRepository {
    private final Map<String, Book> books = new ConcurrentHashMap<>();

    /**
     * Lists all books.
     *
     * @return snapshot of all books
     */
    public Collection<Book> listAll() {
        return List.copyOf(books.values());
    }

    /**
     * Adds a new book.
     *
     * @param book book to add
     * @throws BookAlreadyExistsException if a book with that isbn already exists
     */
    public synchronized void add(Book book) throws BookAlreadyExistsException {
        if (books.putIfAbsent(book.getIsbn(), book) != null) {
            throw new BookAlreadyExistsException(book.getIsbn());
        }
    }

    /**
     * Deletes the book with the given isbn.
     *
     * @param isbn isbn
     * @throws BookNotFoundException if a book with the isbn doesn't exist
     */
    public synchronized void delete(String isbn) throws BookNotFoundException {
        if (books.remove(isbn) == null) {
            throw new BookNotFoundException(isbn);
        }
    }

    /**
     * Updates the book with the given isbn to the given values.
     * <p>
     * The new book may carry a different isbn. In that case, the book is moved to the new isbn,
     * unless another book already uses it. The update is atomic: on failure, nothing is changed.
     *
     * @param isbn    isbn of the book to update
     * @param newBook new values
     * @throws BookNotFoundException      if a book with the isbn doesn't exist
     * @throws BookAlreadyExistsException if the isbn changes and a book with the new isbn already exists
     */
    public synchronized void update(String isbn, Book newBook) throws BookNotFoundException, BookAlreadyExistsException {
        if (!books.containsKey(isbn)) {
            throw new BookNotFoundException(isbn);
        }
        if (!isbn.equals(newBook.getIsbn()) && books.containsKey(newBook.getIsbn())) {
            throw new BookAlreadyExistsException(newBook.getIsbn());
        }

        books.remove(isbn);
        books.put(newBook.getIsbn(), newBook);
    }
}
