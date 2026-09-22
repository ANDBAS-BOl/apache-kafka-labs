package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.outbox.relay;

import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.outbox.entity.OutboxEventEntity;
import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.outbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Relay del Outbox (message relay / poller). Periódicamente lee los eventos
 * pendientes de la tabla {@code outbox_events} y los publica a Kafka.
 * <p>
 * Se ejecuta dentro de una transacción JPA: los eventos que se publican con
 * éxito se marcan como {@code processed=true} (via dirty checking) y ese cambio
 * se confirma al terminar. Si el broker no está disponible o la publicación
 * falla, la fila queda pendiente y se reintenta en la siguiente pasada.
 * <p>
 * Semántica de entrega: <b>at-least-once</b>. Si la app cae después de publicar
 * a Kafka pero antes de confirmar la marca, el evento se reenviará; por eso los
 * consumidores deben ser idempotentes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:5000}")
    @Transactional("transactionManager")
    public void publishPendingEvents() {
        List<OutboxEventEntity> pending = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();
        if (pending.isEmpty()) {
            return;
        }

        log.info("Outbox relay: {} evento(s) pendiente(s) de publicar.", pending.size());

        for (OutboxEventEntity row : pending) {
            try {
                Object event = objectMapper.readValue(row.getPayload(), Class.forName(row.getEventType()));

                // Publicación dentro de una transacción Kafka propia (reutiliza el productor transaccional).
                kafkaTemplate.executeInTransaction(template ->
                        template.send(row.getTopic(), row.getAggregateId(), event));

                row.setProcessed(true);
                row.setProcessedAt(Instant.now());
                log.info("Outbox -> Kafka topic '{}' evento {} (transferencia {})",
                        row.getTopic(), row.getEventType(), row.getAggregateId());
            } catch (Exception ex) {
                // No confirmamos la fila: seguirá pendiente y se reintentará.
                log.warn("No se pudo publicar el evento outbox {} (se reintentará): {}",
                        row.getId(), ex.getMessage());
            }
        }
    }
}
