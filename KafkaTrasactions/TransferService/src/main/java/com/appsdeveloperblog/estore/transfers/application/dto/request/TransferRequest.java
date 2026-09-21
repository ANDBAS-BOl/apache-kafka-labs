package com.appsdeveloperblog.estore.transfers.application.dto.request;

import java.math.BigDecimal;

/**
 * DTO HTTP de entrada. Transporta los datos de la petición de transferencia
 * desde la capa de infraestructura hacia la de aplicación, sin exponer el
 * modelo de dominio al exterior.
 */
public record TransferRequest(
        String senderId,
        String recepientId,
        BigDecimal amount
) {
}
