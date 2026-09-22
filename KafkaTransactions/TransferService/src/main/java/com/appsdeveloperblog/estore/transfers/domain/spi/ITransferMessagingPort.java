package com.appsdeveloperblog.estore.transfers.domain.spi;

import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;

/**
 * Puerto de salida (output/SPI port) hacia la mensajería (Kafka).
 * Las implementaciones publican los eventos correspondientes; los envíos
 * participan en la transacción Kafka abierta por la capa de aplicación.
 */
public interface ITransferMessagingPort {

    void sendWithdrawalEvent(TransferModel transfer);

    void sendDepositEvent(TransferModel transfer);
}
