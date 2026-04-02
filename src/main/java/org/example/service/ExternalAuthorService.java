package org.example.service;

import org.example.dto.AuthorRequest;
import org.example.dto.AuthorResponse;
import org.example.dto.ExternalAuthorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Service для интеграции с микросервисом South America Authors через Kafka
 */
@Service
public class ExternalAuthorService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalAuthorService.class);

    private final KafkaTemplate<String, AuthorRequest> kafkaTemplate;
    
    // Хранилище для корреляции запросов и ответов
    private final Map<String, CompletableFuture<AuthorResponse>> pendingRequests = new ConcurrentHashMap<>();

    @Value("${kafka.topics.author-request}")
    private String authorRequestTopic;
    
    @Value("${kafka.request.timeout:5000}")
    private long requestTimeout;

    public ExternalAuthorService(KafkaTemplate<String, AuthorRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Получить всех авторов из внешнего сервиса
     */
    public List<ExternalAuthorDto> getAllSouthAmericaAuthors() {
        return getSouthAmericaAuthors(null);
    }

    /**
     * Получить авторов из внешнего сервиса по имени через Kafka
     *
     * @param name имя автора для поиска (опциональный параметр)
     * @return список авторов
     */
    public List<ExternalAuthorDto> getSouthAmericaAuthors(String name) {
        // Генерируем уникальный ID запроса для корреляции
        String requestId = UUID.randomUUID().toString();
        
        try {
            if (name != null && !name.trim().isEmpty()) {
                logger.info("=== Отправка запроса в Kafka ===");
                logger.info("Request ID: {}", requestId);
                logger.info("Fetching South America authors by name: {}", name);
            } else {
                logger.info("=== Отправка запроса в Kafka ===");
                logger.info("Request ID: {}", requestId);
                logger.info("Fetching all South America authors");
            }

            // Создаем запрос
            AuthorRequest request = new AuthorRequest(requestId, name);
            
            // Создаем CompletableFuture для ожидания ответа
            CompletableFuture<AuthorResponse> responseFuture = new CompletableFuture<>();
            pendingRequests.put(requestId, responseFuture);

            // Отправляем запрос в Kafka
            kafkaTemplate.send(authorRequestTopic, requestId, request)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        logger.error("Failed to send request to Kafka: {}", ex.getMessage(), ex);
                        responseFuture.completeExceptionally(ex);
                        pendingRequests.remove(requestId);
                    } else {
                        logger.info("Request sent to Kafka successfully. Topic: {}, Partition: {}, Offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    }
                });

            // Ожидаем ответ с таймаутом
            AuthorResponse response = responseFuture.get(requestTimeout, TimeUnit.MILLISECONDS);
            
            // Удаляем запрос из хранилища
            pendingRequests.remove(requestId);

            if (response.isSuccess()) {
                logger.info("=== Получен успешный ответ из Kafka ===");
                logger.info("Request ID: {}", response.getRequestId());
                logger.info("Successfully fetched {} authors from external service", response.getAuthors().size());
                return response.getAuthors();
            } else {
                logger.error("=== Получен ответ с ошибкой из Kafka ===");
                logger.error("Request ID: {}", response.getRequestId());
                logger.error("Error: {}", response.getErrorMessage());
                return Collections.emptyList();
            }
            
        } catch (java.util.concurrent.TimeoutException e) {
            logger.error("Timeout waiting for response from Kafka for request ID: {}", requestId);
            pendingRequests.remove(requestId);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Unexpected error while fetching external authors via Kafka: {}", e.getMessage(), e);
            pendingRequests.remove(requestId);
            return Collections.emptyList();
        }
    }
    
    /**
     * Обработка ответа из Kafka (вызывается Kafka Listener)
     * 
     * @param response ответ от микросервиса
     */
    public void handleAuthorResponse(AuthorResponse response) {
        String requestId = response.getRequestId();
        CompletableFuture<AuthorResponse> future = pendingRequests.get(requestId);
        
        if (future != null) {
            logger.info("Completing request {} with response", requestId);
            future.complete(response);
        } else {
            logger.warn("Received response for unknown request ID: {}", requestId);
        }
    }
}
