package com.appsdeveloperblog.estore.transfers.domain.exception;

/**
 * Excepción de negocio que indica que la transferencia no pudo completarse.
 * Al ser una {@link RuntimeException}, propaga y provoca el rollback de la
 * transacción Kafka iniciada en la capa de aplicación.
 */
public class TransferServiceException extends RuntimeException {

    public TransferServiceException(String message) {
        super(message);
    }

    public TransferServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferServiceException(Throwable cause) {
        super(cause);
    }
}
