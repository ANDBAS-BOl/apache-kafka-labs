package com.appandres.productsmicroservice.infraestructure.adapters.kafka.config;

import com.appandres.core.domain.event.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ProducerFactory<String, ProductCreatedEvent> productCreatedEventProducerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate(
            ProducerFactory<String, ProductCreatedEvent> productCreatedEventProducerFactory) {
        return new KafkaTemplate<>(productCreatedEventProducerFactory);
    }

    @Bean
    NewTopic productCreatedTopic(@Value("${app.kafka.product-created-topic}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }

    @Bean
    NewTopic topic2(@Value("${app.kafka.topic2}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }

}
