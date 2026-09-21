package com.appandres.productsmicroservice.infraestructure.adapters.kafka;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.productsmicroservice.domain.exception.ProductCreatedEventPublishException;
import com.appandres.productsmicroservice.domain.spi.IProductMessagingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProductMessagingAdapter implements IProductMessagingPort {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.product-created-topic}")
    private String productCreatedTopic;

    @Value("${app.kafka.topic2}")
    private String topic2;

    @Override
    public void sendProductCreatedEvent(ProductCreatedEvent event) {

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                productCreatedTopic, event.productId(), event);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

        try {
            SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send(
                    record
/*                    productCreatedTopic,
                    //topic2,
                    event.productId(),
                    event*/
            ).get();
            if (result != null && result.getRecordMetadata() != null) {
                log.info(
                        "ProductCreatedEvent sincrono topic={} partition={} offset={} productId={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.productId()
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProductCreatedEventPublishException(
                    "Interrumpido al publicar ProductCreatedEvent para productId=" + event.productId(),
                    e
            );
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new ProductCreatedEventPublishException(
                    "No se pudo publicar ProductCreatedEvent para productId=" + event.productId(),
                    cause
            );
        }
    }

    /**
     * Metodo asincronico para el envio de mensajes a kafa
     */
    @Override
    public void sendProductCreatedEventAsync(ProductCreatedEvent event) {
        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate.send(
                productCreatedTopic,
                event.productId(),
                event
        );
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("No se pudo publicar ProductCreatedEvent para productId={}", event.productId(), ex);
            } else if (result != null && result.getRecordMetadata() != null) {
                log.info(
                        "Evento publicado topic={} partition={} offset={} productId={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.productId()
                );
            }
        });
    }
}
