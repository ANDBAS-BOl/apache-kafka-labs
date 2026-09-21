package com.appandres.emailnotification.application.port.output;

import com.appandres.core.domain.event.ProductCreatedEvent;

public interface ProductDownstreamVerificationPort {

    void verifyBeforeProcessing(ProductCreatedEvent event);
}
