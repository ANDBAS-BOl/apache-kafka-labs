package com.appsdeveloperblog.estore.transfers.domain.model;

import java.math.BigDecimal;

/**
 * Modelo de dominio puro (sin dependencias de framework) que representa
 * una transferencia de dinero entre dos cuentas.
 */
public record TransferModel(
        String transferId,
        String senderId,
        String recepientId,
        BigDecimal amount
) {
}
