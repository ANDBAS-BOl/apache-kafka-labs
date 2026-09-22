package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.jpa.adapter;

import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferPersistencePort;
import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.jpa.entity.TransferEntity;
import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.jpa.mapper.ITransferEntityMapper;
import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.jpa.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida JPA. Implementa el puerto de persistencia del dominio
 * traduciendo el modelo a entidad y delegando en el repositorio Spring Data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferPersistenceAdapter implements ITransferPersistencePort {

    private final TransferRepository transferRepository;
    private final ITransferEntityMapper transferEntityMapper;

    @Override
    public TransferModel save(TransferModel transfer) {
        TransferEntity saved = transferRepository.save(transferEntityMapper.toEntity(transfer));
        log.info("Persisted transfer with id '{}'.", saved.getTransferId());
        return transferEntityMapper.toModel(saved);
    }
}
