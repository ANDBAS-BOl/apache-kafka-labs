package com.appsdeveloperblog.estore.transfers.domain.spi;

import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;

/**
 * Puerto de salida (output/SPI port) hacia la persistencia.
 * Las implementaciones guardan la transferencia en el almacén de datos y
 * devuelven el modelo persistido.
 */
public interface ITransferPersistencePort {

    TransferModel save(TransferModel transfer);
}
