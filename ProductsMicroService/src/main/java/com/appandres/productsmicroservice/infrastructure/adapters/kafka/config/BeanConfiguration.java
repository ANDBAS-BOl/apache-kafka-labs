package com.appandres.productsmicroservice.infrastructure.adapters.kafka.config;

import com.appandres.productsmicroservice.domain.api.IProductServicePort;
import com.appandres.productsmicroservice.domain.spi.IProductMessagingPort;
import com.appandres.productsmicroservice.domain.spi.IProductPersistencePort;
import com.appandres.productsmicroservice.domain.usecase.ProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IProductServicePort productServicePort(
            IProductPersistencePort productPersistencePort,
            IProductMessagingPort productMessagingPort) {

        // Aquí instanciamos el caso de uso manualmente y le pasamos sus dependencias (SPIs)
        return new ProductUseCase(productPersistencePort, productMessagingPort);
    }
}