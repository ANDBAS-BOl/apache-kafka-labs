package com.appandres.emailnotification;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.emailnotification.application.handler.ProductCreatedEventHandler;
import com.appandres.emailnotification.application.port.output.ProcessedEventOutputPort;
import com.appandres.emailnotification.application.port.output.ProductDownstreamVerificationPort;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EmbeddedKafka(topics = "product-created-events-topic", partitions = 1)
@SpringBootTest(properties = {
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
public class ProductCreatedEventHandlerIntegrationTest {

    // Reemplazan a los adapters reales (JPA y HTTP/RestTemplate) por dobles de prueba.
    @MockitoBean
    private ProductDownstreamVerificationPort productDownstreamVerificationPort;

    @MockitoBean
    private ProcessedEventOutputPort processedEventOutputPort;

    // Spy = el handler REAL, pero podemos espiar/verificar sus llamadas.
    @MockitoSpyBean
    private ProductCreatedEventHandler productCreatedEventHandler;

    // Nos permite publicar mensajes en el Kafka embebido.
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testProductCreatedEventHandler_OnProductCreated_HandlesEvent() throws Exception {
        // ---------- Arrange ----------
        ProductCreatedEvent productCreatedEvent =
                new ProductCreatedEvent(UUID.randomUUID().toString(), "Test Product", new BigDecimal(10), 1);

        String messageId = UUID.randomUUID().toString();
        String messageKey = productCreatedEvent.productId();

        // El tipo debe ser <String, Object> para calzar con KafkaTemplate<String, Object>.
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "product-created-events-topic",
                messageKey,
                productCreatedEvent);

        // El handler necesita el header "messageId"; la key la rellena Spring sola en el consumer.
        record.headers().add("messageId", messageId.getBytes());

        // Unico metodo que DEVUELVE valor -> se stubbea. Los void (verify/markAsProcessed) NO.
        when(processedEventOutputPort.isAlreadyProcessed(anyString())).thenReturn(false);

        // ---------- Act ----------
        // send() es asincrono; .get() bloquea hasta que Kafka confirma la publicacion.
        kafkaTemplate.send(record).get();

        // ---------- Assert ----------
        // Capturamos lo que le llego al handler DESPUES de viajar por Kafka -> consumer -> handler.
        ArgumentCaptor<ProductCreatedEvent> eventCaptor = ArgumentCaptor.forClass(ProductCreatedEvent.class);
        ArgumentCaptor<String> messageIdCaptor = ArgumentCaptor.forClass(String.class);

        // timeout(5000): como el consumo es asincrono, esperamos hasta 5s a que ocurra la llamada.
        verify(productCreatedEventHandler, timeout(5000).times(1))
                .process(eventCaptor.capture(), messageIdCaptor.capture());

        assertEquals(messageId, messageIdCaptor.getValue());
        assertEquals(productCreatedEvent.productId(), eventCaptor.getValue().productId());
        assertEquals(productCreatedEvent.title(), eventCaptor.getValue().title());
    }
}
