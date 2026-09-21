package com.appandres.productsmicroservice.infraestructure.adapters.in.rest.exception;

import com.appandres.productsmicroservice.domain.exception.ProductCreatedEventPublishException;
import com.appandres.productsmicroservice.infraestructure.adapters.in.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductCreatedEventPublishException.class)
    public ResponseEntity<ErrorResponse> handleProductCreatedEventPublish(ProductCreatedEventPublishException ex) {
        ErrorResponse body = new ErrorResponse(
                "PRODUCT_CREATED_EVENT_PUBLISH_FAILED",
                ex.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
