package com.appsdeveloperblog.estore.transfers.infrastructure.adapters.in.rest.exception;

import com.appsdeveloperblog.estore.transfers.domain.exception.TransferServiceException;
import com.appsdeveloperblog.estore.transfers.infrastructure.adapters.in.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Traduce las excepciones de dominio a respuestas HTTP. Aísla al cliente de
 * los detalles internos y mantiene la coherencia con el resto de servicios.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferServiceException.class)
    public ResponseEntity<ErrorResponse> handleTransferService(TransferServiceException ex) {
        ErrorResponse body = new ErrorResponse(
                "TRANSFER_FAILED",
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
