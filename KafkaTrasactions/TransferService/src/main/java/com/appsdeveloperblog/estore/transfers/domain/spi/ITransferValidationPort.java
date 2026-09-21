package com.appsdeveloperblog.estore.transfers.domain.spi;

import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;

/**
 * Puerto de salida (output/SPI port) hacia un servicio remoto de validación.
 * Simula la llamada a un microservicio destino que puede fallar; ese fallo es
 * el que dispara el rollback de la transacción Kafka.
 */
public interface ITransferValidationPort {

    void validateTransfer(TransferModel transfer);
}
