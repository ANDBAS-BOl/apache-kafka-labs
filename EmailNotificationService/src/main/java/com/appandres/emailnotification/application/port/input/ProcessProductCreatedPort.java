package com.appandres.emailnotification.application.port.input;

import com.appandres.core.domain.event.ProductCreatedEvent;

public interface ProcessProductCreatedPort {

    void process(ProductCreatedEvent event, String messageId);

}
