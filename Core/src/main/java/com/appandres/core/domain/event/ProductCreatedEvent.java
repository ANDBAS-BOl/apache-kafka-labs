package com.appandres.core.domain.event;

import java.math.BigDecimal;

/**
 * Contrato de integración compartido entre bounded contexts.
 *
 * <p>Representa el hecho de dominio "Product created" publicado por el productor
 * ({@code ProductsMicroService}) y consumido por cualquier suscriptor
 * (ej. {@code EmailNotificationService}).</p>
 *
 * <p>En términos de DDD corresponde al <em>Published Language</em> / <em>Shared Kernel</em>:
 * cambios en este tipo rompen el contrato de todos los consumidores, por lo que debe
 * evolucionar de forma compatible (añadir campos, nunca eliminarlos ni renombrarlos).</p>
 */
public record ProductCreatedEvent(
        String productId,
        String title,
        BigDecimal price,
        Integer quantity) {
}
