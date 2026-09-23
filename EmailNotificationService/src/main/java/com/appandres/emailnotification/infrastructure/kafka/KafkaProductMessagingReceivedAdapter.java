package com.appandres.emailnotification.infrastructure.kafka;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.emailnotification.application.port.input.ProcessProductCreatedPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(topics = "product-created-events-topic")
public class KafkaProductMessagingReceivedAdapter {

    private final ProcessProductCreatedPort processProductCreatedPort;

    @KafkaHandler
    public void onProductCreated(@Payload ProductCreatedEvent event,
                                 @Header(value = "messageId") String messageId,
                                 @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        log.info("Received product created event {}", event.title());
        processProductCreatedPort.process(event, messageId);
    }
}
