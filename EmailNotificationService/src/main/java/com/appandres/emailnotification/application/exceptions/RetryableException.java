package com.appandres.emailnotification.application.exceptions;

/**
 * Marcador semántico para fallos <strong>transitorios</strong> ocurridos durante el
 * procesamiento de un mensaje (p. ej. timeouts de red, 5xx temporales, indisponibilidad
 * momentánea de un servicio dependiente).
 *
 * <p>El adaptador de mensajería (Kafka {@code DefaultErrorHandler}) interpretará esta
 * excepción aplicando la política de reintentos configurada (BackOff) antes de enviar
 * el registro al Dead Letter Topic.</p>
 *
 * <p>Vive en la capa de aplicación porque es una semántica de orquestación de casos de
 * uso, no una regla de dominio: el dominio debe permanecer agnóstico al transporte.</p>
 */
public class RetryableException extends RuntimeException {

    public RetryableException(String message) {
        super(message);
    }

    public RetryableException(Throwable cause) {
        super(cause);
    }

    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
