package com.appsdeveloperblog.estore.transfers.infrastructure.adapters.out.outbox;

import com.appsdeveloperblog.estore.transfers.domain.exception.TransferServiceException;
import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferMessagingPort;
import com.appsdeveloperblog.estore.transfers.infrastructure.adapters.out.outbox.entity.OutboxEventEntity;
import com.appsdeveloperblog.estore.transfers.infrastructure.adapters.out.outbox.repository.OutboxEventRepository;
import com.appsdeveloperblog.payments.ws.core.events.DepositRequestedEvent;
import com.appsdeveloperblog.payments.ws.core.events.WithdrawalRequestedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Implementación del puerto de mensajería mediante el patrón Outbox.
 * <p>
 * En lugar de enviar el evento directamente a Kafka, lo <b>serializa y lo
 * guarda como una fila en la tabla {@code outbox_events}</b>. Como este método
 * se ejecuta dentro de la transacción JPA abierta por la capa de aplicación, la
 * escritura del evento es atómica con la de los datos de negocio: si la
 * transacción hace rollback, el evento tampoco queda registrado y nunca se
 * publicará. La publicación real a Kafka la realiza {@code OutboxRelayScheduler}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMessagingAdapter implements ITransferMessagingPort {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.withdraw-money-topic}")
    private String withdrawTopic;

    @Value("${app.kafka.deposit-money-topic}")
    private String depositTopic;

    @Override
    public void sendWithdrawalEvent(TransferModel transfer) {
        WithdrawalRequestedEvent event = new WithdrawalRequestedEvent(
                transfer.senderId(), transfer.recepientId(), transfer.amount());
        appendToOutbox(transfer.transferId(), withdrawTopic, event);
    }

    @Override
    public void sendDepositEvent(TransferModel transfer) {
        DepositRequestedEvent event = new DepositRequestedEvent(
                transfer.senderId(), transfer.recepientId(), transfer.amount());
        appendToOutbox(transfer.transferId(), depositTopic, event);
    }

    private void appendToOutbox(String aggregateId, String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEventEntity row = OutboxEventEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .aggregateId(aggregateId)
                    .eventType(event.getClass().getName())
                    .topic(topic)
                    .payload(payload)
                    .processed(false)
                    .createdAt(Instant.now())
                    .build();

            outboxRepository.save(row);
            log.info("Outbox <- {} para la transferencia {}", event.getClass().getSimpleName(), aggregateId);
        } catch (JsonProcessingException ex) {
            throw new TransferServiceException("No se pudo serializar el evento para el outbox", ex);
        }
    }
}
