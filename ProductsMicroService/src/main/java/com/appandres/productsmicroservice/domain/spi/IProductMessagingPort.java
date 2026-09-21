package com.appandres.productsmicroservice.domain.spi;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.productsmicroservice.domain.exception.ProductCreatedEventPublishException;

public interface IProductMessagingPort {
    /**
     * Envío sincrónico del evento. Puede lanzar {@link ProductCreatedEventPublishException}
     * si la infraestructura de mensajería falla.
     */
    void sendProductCreatedEvent(ProductCreatedEvent event);

    void sendProductCreatedEventAsync(ProductCreatedEvent event);
}
