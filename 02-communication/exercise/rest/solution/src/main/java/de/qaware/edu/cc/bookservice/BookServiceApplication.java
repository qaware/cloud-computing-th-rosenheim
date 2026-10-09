package de.qaware.edu.cc.bookservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main Spring boot application class.
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Book Service", version = "1.0.1", description = "REST API to manage the books of a library"))
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}
