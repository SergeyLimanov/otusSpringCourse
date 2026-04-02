package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.dto.AuthorRequest;
import org.example.dto.AuthorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka для интеграции с микросервисом авторов
 */
@Slf4j
@Configuration
public class KafkaIntegrationConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.author-response}")
    private String authorResponseTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    /**
     * Конфигурация Producer Factory для AuthorRequest
     */
    @Bean
    public ProducerFactory<String, AuthorRequest> authorRequestProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Kafka Template для отправки AuthorRequest
     */
    @Bean
    public KafkaTemplate<String, AuthorRequest> authorRequestKafkaTemplate() {
        return new KafkaTemplate<>(authorRequestProducerFactory());
    }

    /**
     * Конфигурация Consumer Factory для AuthorResponse
     */
    @Bean
    public ConsumerFactory<String, AuthorResponse> authorResponseConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AuthorResponse.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(AuthorResponse.class)
        );
    }

    /**
     * Kafka Template для получения AuthorResponse
     */
    @Bean
    public KafkaTemplate<String, AuthorResponse> authorResponseKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(new HashMap<>()));
    }

    /**
     * Listener Container для получения ответов из Kafka
     */
    @Bean
    public KafkaMessageListenerContainer<String, AuthorResponse> authorResponseListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties(authorResponseTopic);
        return new KafkaMessageListenerContainer<>(
            authorResponseConsumerFactory(),
            containerProps
        );
    }

    /**
     * Kafka Listener Container Factory для @KafkaListener аннотаций
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthorResponse> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AuthorResponse> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(authorResponseConsumerFactory());
        return factory;
    }
}
