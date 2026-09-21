package com.appandres.emailnotification.application.exceptions;

/**
 * Marcador semántico para fallos <strong>permanentes / no recuperables</strong> ocurridos
 * durante el procesamiento de un mensaje (p. ej. payload inválido, violación de invariantes
 * de dominio, recursos inexistentes, errores 4xx de un servicio dependiente).
 *
 * <p>El adaptador de mensajería (Kafka {@code DefaultErrorHandler}) interpretará esta
 * excepción saltándose los reintentos y publicando el registro directamente en el
 * Dead Letter Topic.</p>
 *
 * <p>Vive en la capa de aplicación porque es una semántica de orquestación de casos de
 * uso, no una regla de dominio: el dominio debe permanecer agnóstico al transporte.</p>
 */
public class NotRetryableException extends RuntimeException {

    public NotRetryableException(String message) {
        super(message);
    }

    public NotRetryableException(Throwable cause) {
        super(cause);
    }

    public NotRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
