package com.appsdeveloperblog.estore.transfers.application.handler.impl;

import com.appsdeveloperblog.estore.transfers.application.dto.request.TransferRequest;
import com.appsdeveloperblog.estore.transfers.application.handler.ITransferHandler;
import com.appsdeveloperblog.estore.transfers.application.mapper.ITransferDtoMapper;
import com.appsdeveloperblog.estore.transfers.domain.api.ITransferServicePort;
import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler de aplicación. Define la frontera transaccional <b>de base de datos</b>
 * (Outbox): la persistencia de la transferencia y el registro de los eventos en
 * la tabla {@code outbox_events} ocurren en una única transacción JPA. Si la
 * validación remota falla, se hace rollback de todo y no queda nada guardado ni,
 * por tanto, nada pendiente de publicar.
 */
@Service
@RequiredArgsConstructor
public class TransferHandlerImpl implements ITransferHandler {

    private final ITransferServicePort transferServicePort;
    private final ITransferDtoMapper transferDtoMapper;

    @Override
    @Transactional("transactionManager")
    public boolean transfer(TransferRequest transferRequest) {
        TransferModel transferModel = transferDtoMapper.toTransferModel(transferRequest);
        return transferServicePort.transfer(transferModel);
    }
}
