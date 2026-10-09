package de.qaware.edu.cc.bookconsumer;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory read model that is built from the stream of book events.
 */
@Component
public class Inventory {

    private final Map<String, BookStatus> books = new ConcurrentHashMap<>();

    public void apply(BookEvent event) {
        switch (event.type()) {
            case ADDED, RETURNED -> books.put(event.isbn(), new BookStatus(event.isbn(), event.title(), event.author(), true));
            case BORROWED -> books.put(event.isbn(), new BookStatus(event.isbn(), event.title(), event.author(), false));
            case REMOVED -> books.remove(event.isbn());
        }
    }

    public Collection<BookStatus> findAll() {
        return List.copyOf(books.values());
    }
}
