package com.appandres.emailnotification.infrastructure.out.jpa.repository;

import com.appandres.emailnotification.infrastructure.out.jpa.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, Long> {

    boolean existsByMessageId(String messageId);
}