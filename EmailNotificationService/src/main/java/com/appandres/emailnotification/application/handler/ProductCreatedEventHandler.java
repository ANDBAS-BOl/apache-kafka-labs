package com.appandres.emailnotification.application.handler;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.emailnotification.application.exceptions.NotRetryableException;
import com.appandres.emailnotification.application.port.input.ProcessProductCreatedPort;
import com.appandres.emailnotification.application.port.output.ProcessedEventOutputPort;
import com.appandres.emailnotification.application.port.output.ProductDownstreamVerificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCreatedEventHandler implements ProcessProductCreatedPort {

    private static String REQUEST_URL = "http://localhost:8082/response/200";

    private final ProductDownstreamVerificationPort productDownstreamVerificationPort;
    private final ProcessedEventOutputPort processedEventOutputPort;

    @Override
    public void process(ProductCreatedEvent event, String messageId) {
        if (processedEventOutputPort.isAlreadyProcessed(messageId)) {
            throw new NotRetryableException("Duplicate event detected, messageId=" + messageId + " already processed");
        }
        log.info("Processing product created event {}", event.title());
        productDownstreamVerificationPort.verifyBeforeProcessing(event);
        processedEventOutputPort.markAsProcessed(messageId, event.productId());
        log.info("Event persisted with messageId={}", messageId);
    }
}
