package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración del cliente HTTP usado por los adaptadores de salida REST.
 */
@Configuration
public class RestClientConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
