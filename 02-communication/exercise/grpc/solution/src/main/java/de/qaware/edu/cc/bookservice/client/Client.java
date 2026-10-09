package de.qaware.edu.cc.bookservice.client;

import de.qaware.edu.cc.generated.BookProto;
import de.qaware.edu.cc.generated.BookServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final String SERVER = "localhost:12345";
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(SERVER).usePlaintext().build();
        try {
            BookServiceGrpc.BookServiceBlockingStub bookService = BookServiceGrpc.newBlockingStub(channel);
            runMenu(bookService);
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void runMenu(BookServiceGrpc.BookServiceBlockingStub bookService) {
        System.out.println("Welcome to the book client. What do you want to do?");

        while (true) {
            System.out.println("list");
            System.out.println("add");
            System.out.println("update");
            System.out.println("delete");

            System.out.print("> ");
            if (!SCANNER.hasNextLine()) {
                return;
            }
            String line = SCANNER.nextLine();
            if (line.isEmpty()) {
                return;
            }

            try {
                switch (line) {
                    case "list" -> listBooks(bookService);
                    case "add" -> addNewBook(bookService);
                    case "update" -> updateBook(bookService);
                    case "delete" -> deleteBook(bookService);
                    default -> System.out.println("Come again? (Press enter to quit)");
                }
            } catch (StatusRuntimeException e) {
                // The server signals errors (e.g. NOT_FOUND, ALREADY_EXISTS) via the gRPC status
                System.out.println("Server returned an error: " + e.getStatus().getCode());
                System.out.println();
            }
        }
    }

    private static void deleteBook(BookServiceGrpc.BookServiceBlockingStub bookService) {
        System.out.println("Going to delete a book ...");
        System.out.print("Enter ISBN: ");
        String isbn = SCANNER.nextLine();

        bookService.deleteBook(BookProto.Isbn.newBuilder().setValue(isbn).build());
        System.out.println("Deleted book!");
        System.out.println();
    }

    private static void addNewBook(BookServiceGrpc.BookServiceBlockingStub bookService) {
        BookProto.Book addedBook = bookService.addBook(readBook());

        System.out.printf("Added book: %s - %s from %s%n", addedBook.getIsbn(), addedBook.getTitle(), addedBook.getAuthor());
        System.out.println();
    }

    private static void updateBook(BookServiceGrpc.BookServiceBlockingStub bookService) {
        System.out.println("Going to update a book ...");
        System.out.print("Enter ISBN of the book to update: ");
        String isbn = SCANNER.nextLine();

        System.out.println("Enter the new values:");
        BookProto.Book updatedBook = bookService.updateBook(
            BookProto.UpdateBookRequest.newBuilder().setIsbn(isbn).setNewBook(readBook()).build()
        );

        System.out.printf("Updated book: %s - %s from %s%n", updatedBook.getIsbn(), updatedBook.getTitle(), updatedBook.getAuthor());
        System.out.println();
    }

    private static BookProto.Book readBook() {
        System.out.print("Enter ISBN: ");
        String isbn = SCANNER.nextLine();

        System.out.print("Enter title: ");
        String title = SCANNER.nextLine();

        System.out.print("Enter author: ");
        String author = SCANNER.nextLine();

        return BookProto.Book.newBuilder().setIsbn(isbn).setTitle(title).setAuthor(author).build();
    }

    private static void listBooks(BookServiceGrpc.BookServiceBlockingStub bookService) {
        Iterator<BookProto.Book> books = bookService.listBooks(BookProto.ListBooksRequest.getDefaultInstance());
        System.out.println("Available books:");
        while (books.hasNext()) {
            BookProto.Book book = books.next();

            System.out.printf("  %s - %s from %s%n", book.getIsbn(), book.getTitle(), book.getAuthor());
        }
        System.out.println();
    }
}
