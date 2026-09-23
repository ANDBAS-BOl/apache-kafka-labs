package com.appsdeveloperblog.estore.transfers.infrastructure.configuration;

import com.appsdeveloperblog.estore.transfers.domain.api.ITransferServicePort;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferMessagingPort;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferPersistencePort;
import com.appsdeveloperblog.estore.transfers.domain.spi.ITransferValidationPort;
import com.appsdeveloperblog.estore.transfers.domain.usecase.TransferUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring de los beans hexagonales. Instancia manualmente el caso de uso del
 * dominio (que no lleva anotaciones de framework) inyectándole sus puertos SPI.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public ITransferServicePort transferServicePort(
            ITransferPersistencePort transferPersistencePort,
            ITransferMessagingPort transferMessagingPort,
            ITransferValidationPort transferValidationPort) {

        return new TransferUseCase(transferPersistencePort, transferMessagingPort, transferValidationPort);
    }
}
