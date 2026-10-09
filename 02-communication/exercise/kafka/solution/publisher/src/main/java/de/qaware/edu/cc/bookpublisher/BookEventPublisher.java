package de.qaware.edu.cc.bookpublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes book events to Kafka. The ISBN is used as message key, so all events
 * of the same book end up in the same partition and keep their order.
 */
@Component
public class BookEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(BookEventPublisher.class);

    private final KafkaTemplate<String, BookEvent> kafkaTemplate;

    public BookEventPublisher(KafkaTemplate<String, BookEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(BookEvent event) {
        kafkaTemplate.send(KafkaConfig.TOPIC, event.isbn(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        LOG.error("Failed to publish {}", event, ex);
                    } else {
                        LOG.info("Published {} to partition {} at offset {}", event,
                                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}
