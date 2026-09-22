package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entidad de la tabla {@code outbox_events}. Es el corazón del patrón Outbox:
 * cada evento que el dominio quiere publicar se guarda aquí como una fila
 * <b>dentro de la misma transacción de base de datos</b> que los datos de
 * negocio. Un proceso aparte (relay) la lee y la publica a Kafka.
 */
@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEventEntity {

    /** Identificador único de la fila del outbox. */
    @Id
    @Column(nullable = false)
    private String id;

    /** Id de la entidad de negocio a la que pertenece el evento (el transferId). */
    @Column(nullable = false)
    private String aggregateId;

    /** Nombre completo de la clase del evento, para reconstruirlo al publicar. */
    @Column(nullable = false)
    private String eventType;

    /** Topic de Kafka de destino. */
    @Column(nullable = false)
    private String topic;

    /** Evento serializado a JSON. */
    @Lob
    @Column(nullable = false)
    private String payload;

    /** false = pendiente de publicar; true = ya publicado a Kafka. */
    @Column(nullable = false)
    private boolean processed;

    /** Momento en que se guardó en el outbox. */
    @Column(nullable = false)
    private Instant createdAt;

    /** Momento en que se publicó a Kafka (null mientras esté pendiente). */
    @Column
    private Instant processedAt;
}
