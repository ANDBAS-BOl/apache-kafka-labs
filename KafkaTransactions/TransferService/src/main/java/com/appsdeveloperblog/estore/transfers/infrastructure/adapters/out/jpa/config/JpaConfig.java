package com.appsdeveloperblog.estore.transfers.infrastructure.adapters.out.jpa.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 * Declara explícitamente el {@link JpaTransactionManager} con el nombre
 * {@code transactionManager}. Es necesario porque, al existir ya un
 * {@code KafkaTransactionManager}, Spring Boot no autoconfigura el de JPA
 * (su condición es {@code @ConditionalOnMissingBean(TransactionManager.class)}).
 * Este es el gestor que usa el flujo Outbox (escrituras en H2).
 */
@Configuration
public class JpaConfig {

    @Bean("transactionManager")
    JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
