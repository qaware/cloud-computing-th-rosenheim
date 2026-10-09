package de.qaware.edu.cc.bookpublisher;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Creates the topic on startup if it does not exist yet (via Spring's KafkaAdmin).
 */
@Configuration
public class KafkaConfig {

    public static final String TOPIC = "book-events";

    @Bean
    public NewTopic bookEventsTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
