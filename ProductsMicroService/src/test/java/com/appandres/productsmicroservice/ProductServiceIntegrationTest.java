package com.appandres.productsmicroservice;

import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.productsmicroservice.application.dto.request.CreateProductRequest;
import com.appandres.productsmicroservice.application.mapper.IProductDtoMapper;
import com.appandres.productsmicroservice.domain.api.IProductServicePort;
import com.appandres.productsmicroservice.domain.model.ProductModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test") // application-test.properties
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class ProductServiceIntegrationTest {

    private final IProductServicePort productServicePort;

    private final IProductDtoMapper productDtoMapper;

    private final EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private Environment environment;

    @Autowired
    public ProductServiceIntegrationTest(IProductServicePort productServicePort,
                                         IProductDtoMapper productDtoMapper,
                                         EmbeddedKafkaBroker embeddedKafkaBroker) {
        this.productServicePort = productServicePort;
        this.productDtoMapper = productDtoMapper;
        this.embeddedKafkaBroker = embeddedKafkaBroker;
    }

    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;
    private BlockingDeque<ConsumerRecord<String, ProductCreatedEvent>> records;

    @BeforeAll
    void setup() {
        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(environment.getProperty("product-created-events-topic-name"));
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingDeque<>();
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
        //<SystemUndertest>_Condition_ExpectedResult
    void testCreteProduct_whenGivenValidProductDetails_successfullSendsKafkaMessage() throws InterruptedException {
        //Arrange -> Prepare data (mock data)
        String title = "iPhone 11";
        BigDecimal price = new BigDecimal("600");
        Integer quantity = 1;
        CreateProductRequest createProductRequest = new CreateProductRequest(title, price, quantity);
        ProductModel productModel = productDtoMapper.toProductModel(createProductRequest);

        //Act -> Use prepared data with the method to test
        productServicePort.createProduct(productModel);

        //Assert ->  verify that the method return the expect result
        ConsumerRecord<String, ProductCreatedEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        assertNotNull(message.key());
        ProductCreatedEvent productCreatedEvent = message.value();
        assertEquals(createProductRequest.quantity(), productCreatedEvent.quantity());
        assertEquals(createProductRequest.title(), productCreatedEvent.title());
        assertEquals(createProductRequest.price(), productCreatedEvent.price());
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                JsonDeserializer.VALUE_DEFAULT_TYPE, ProductCreatedEvent.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset"));
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }
}
