package com.appsdeveloperblog.estore.transfers.domain.usecase;

import com.appsdeveloperblog.estore.transfers.domain.api.ITransferServicePort;
import com.appsdeveloperblog.estore.transfers.domain.exception.TransferServiceException;
import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferMessagingPort;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferPersistencePort;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferValidationPort;

import java.util.UUID;

/**
 * Implementación del caso de uso de transferencia. Orquesta el flujo:
 * <ol>
 *     <li>Persiste la transferencia en la base de datos.</li>
 *     <li>Registra el evento de retiro (withdrawal).</li>
 *     <li>Invoca la validación remota (puede fallar).</li>
 *     <li>Registra el evento de depósito (deposit).</li>
 * </ol>
 * El dominio solo expresa "quiero publicar estos eventos" a través de
 * {@code ITransferMessagingPort}; no sabe <i>cómo</i> se publican. Con la
 * implementación Outbox, esos "envíos" se traducen en filas de la tabla
 * {@code outbox_events} escritas dentro de la misma transacción JPA que la
 * transferencia. Por tanto, si la validación falla, se hace rollback de todo y
 * no queda nada que publicar (consistencia garantizada, sin doble escritura).
 * <p>
 * Dominio puro: no contiene anotaciones de framework. El cableado se realiza en
 * {@code infraestructure.configuration.BeanConfiguration}.
 */
public class TransferUseCase implements ITransferServicePort {

    private final ITransferPersistencePort persistencePort;
    private final ITransferMessagingPort messagingPort;
    private final ITransferValidationPort validationPort;

    public TransferUseCase(ITransferPersistencePort persistencePort,
                           ITransferMessagingPort messagingPort,
                           ITransferValidationPort validationPort) {
        this.persistencePort = persistencePort;
        this.messagingPort = messagingPort;
        this.validationPort = validationPort;
    }

    @Override
    public boolean transfer(TransferModel transfer) {
        // Asigna un identificador de dominio a la transferencia antes de persistirla.
        TransferModel toProcess = new TransferModel(
                UUID.randomUUID().toString(),
                transfer.senderId(),
                transfer.recepientId(),
                transfer.amount());

        try {
            persistencePort.save(toProcess);

            messagingPort.sendWithdrawalEvent(toProcess);

            // Lógica de negocio remota que puede provocar un error y forzar el rollback.
            validationPort.validateTransfer(toProcess);

            messagingPort.sendDepositEvent(toProcess);
        } catch (TransferServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new TransferServiceException(ex);
        }

        return true;
    }
}
