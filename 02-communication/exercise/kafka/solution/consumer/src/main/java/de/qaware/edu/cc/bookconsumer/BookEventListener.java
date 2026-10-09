package de.qaware.edu.cc.bookconsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes book events from Kafka and updates the inventory.
 */
@Component
public class BookEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(BookEventListener.class);

    private final Inventory inventory;

    public BookEventListener(Inventory inventory) {
        this.inventory = inventory;
    }

    @KafkaListener(topics = "book-events")
    public void onBookEvent(ConsumerRecord<String, BookEvent> record) {
        LOG.info("Received {} from partition {} at offset {}", record.value(), record.partition(), record.offset());
        inventory.apply(record.value());
    }
}
