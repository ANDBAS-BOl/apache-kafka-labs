package com.appandres.emailnotification.application.port.output;

public interface ProcessedEventOutputPort {

    boolean isAlreadyProcessed(String messageId);

    void markAsProcessed(String messageId, String productId);
}
