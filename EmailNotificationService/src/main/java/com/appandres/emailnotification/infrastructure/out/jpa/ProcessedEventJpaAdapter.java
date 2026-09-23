package com.appandres.emailnotification.infrastructure.out.jpa;

import com.appandres.emailnotification.application.port.output.ProcessedEventOutputPort;
import com.appandres.emailnotification.infrastructure.out.jpa.entity.ProcessedEventEntity;
import com.appandres.emailnotification.infrastructure.out.jpa.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessedEventJpaAdapter implements ProcessedEventOutputPort {

    private final ProcessedEventRepository processedEventRepository;

    @Override
    public boolean isAlreadyProcessed(String messageId) {
        return processedEventRepository.existsByMessageId(messageId);
    }

    @Override
    public void markAsProcessed(String messageId, String productId) {
        processedEventRepository.save(new ProcessedEventEntity(messageId, productId));
    }
}
