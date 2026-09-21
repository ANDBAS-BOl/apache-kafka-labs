package com.appandres.productsmicroservice.domain.exception;

/**
 * Indica que no se pudo notificar la creación del producto al exterior (mensajería).
 * Las implementaciones de {@link com.appandres.productsmicroservice.domain.spi.IProductMessagingPort}
 * deben traducir fallos de infraestructura a esta excepción.
 */
public class ProductCreatedEventPublishException extends RuntimeException {

    public ProductCreatedEventPublishException(String message) {
        super(message);
    }

    public ProductCreatedEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
