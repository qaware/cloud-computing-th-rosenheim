package de.qaware.edu.cc.bookconsumer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Exposes the inventory that was built from the consumed events.
 */
@RestController
@RequestMapping(value = "/api/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
public class InventoryController {

    private final Inventory inventory;

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    @GetMapping
    public Collection<BookStatus> inventory() {
        return inventory.findAll();
    }
}
