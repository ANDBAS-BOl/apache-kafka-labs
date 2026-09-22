package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.outbox.repository;

import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.outbox.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio del outbox. Devuelve los eventos pendientes en orden de creación
 * para respetar el orden de publicación.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, String> {

    List<OutboxEventEntity> findByProcessedFalseOrderByCreatedAtAsc();
}
