package com.appsdeveloperblog.estore.transfers.infrastructure.adapters.out.jpa.repository;

import com.appsdeveloperblog.estore.transfers.infrastructure.adapters.out.jpa.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para persistir {@link TransferEntity} en la BD.
 */
@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, String> {
}
