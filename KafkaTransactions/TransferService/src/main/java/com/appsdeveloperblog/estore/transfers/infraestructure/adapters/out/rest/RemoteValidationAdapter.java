package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.rest;

import com.appsdeveloperblog.estore.transfers.domain.exception.TransferServiceException;
import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Adaptador de salida HTTP. Invoca un microservicio remoto de validación.
 * Si el servicio no está disponible (o la llamada falla), lanza una
 * {@link TransferServiceException}, lo que provoca el rollback de la
 * transacción Kafka en curso.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteValidationAdapter implements ITransferValidationPort {

    private final RestTemplate restTemplate;

    @Value("${app.remote.validation-url}")
    private String validationUrl;

    @Override
    public void validateTransfer(TransferModel transfer) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    validationUrl, HttpMethod.GET, null, String.class);

            if (response.getStatusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                throw new TransferServiceException("Destination Microservice not available");
            }

            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Received response from remote validation service: {}", response.getBody());
            }
        } catch (TransferServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Remote validation call failed: {}", ex.getMessage(), ex);
            throw new TransferServiceException(ex);
        }
    }
}
