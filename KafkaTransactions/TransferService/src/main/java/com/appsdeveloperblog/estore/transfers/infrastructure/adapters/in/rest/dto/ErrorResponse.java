package com.appsdeveloperblog.estore.transfers.infrastructure.adapters.in.rest.dto;

import java.time.Instant;

/**
 * DTO de salida para respuestas de error HTTP.
 */
public record ErrorResponse(String code, String message, Instant timestamp) {
}
