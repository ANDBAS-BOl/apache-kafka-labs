package com.appandres.emailnotification.infraestructure.http;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.emailnotification.application.exceptions.NotRetryableException;
import com.appandres.emailnotification.application.exceptions.RetryableException;
import com.appandres.emailnotification.application.port.output.ProductDownstreamVerificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestTemplateProductDownstreamVerificationAdapter implements ProductDownstreamVerificationPort {

    private final RestTemplate restTemplate;

    @Value("${app.downstream.product-ack.url:http://localhost:8082/response/200}")
    private String requestUrl;

    @Override
    public void verifyBeforeProcessing(ProductCreatedEvent event) {
        log.info("Verifying downstream before processing product event id={}", event.title() + " With productID: " + event.productId());
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Downstream response: {}", response.getBody());
            }
        } catch (ResourceAccessException ex) {
            log.error("Resource access exception", ex);
            throw new RetryableException("Resource access exception", ex);
        } catch (HttpServerErrorException ex) {
            log.error("HTTP server error", ex);
            throw new RetryableException("HTTP server error", ex);
        } catch (HttpClientErrorException ex) {
            log.error("HTTP client error", ex);
            throw new NotRetryableException("HTTP client error", ex);
        } catch (Exception ex) {
            log.error("Unexpected error during downstream verification", ex);
            throw new NotRetryableException("Unexpected error during downstream verification", ex);
        }
    }
}
