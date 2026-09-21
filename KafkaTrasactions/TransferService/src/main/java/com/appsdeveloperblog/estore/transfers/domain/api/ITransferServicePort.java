package com.appsdeveloperblog.estore.transfers.domain.api;

import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;

/**
 * Puerto de entrada (input port) del dominio. Define el caso de uso de negocio
 * de transferir dinero, sin acoplarse a ningún mecanismo de entrega (REST, etc.).
 */
public interface ITransferServicePort {

    boolean transfer(TransferModel transfer);
}
