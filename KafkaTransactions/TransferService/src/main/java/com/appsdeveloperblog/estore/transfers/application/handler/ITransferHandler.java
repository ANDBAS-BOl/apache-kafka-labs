package com.appsdeveloperblog.estore.transfers.application.handler;

import com.appsdeveloperblog.estore.transfers.application.dto.request.TransferRequest;

/**
 * Interfaz del handler de aplicación. Recibe DTOs desde la infraestructura,
 * los adapta al dominio y delega en el puerto de entrada, definiendo además
 * la frontera transaccional.
 */
public interface ITransferHandler {

    boolean transfer(TransferRequest transferRequest);
}
