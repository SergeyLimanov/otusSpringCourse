package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthorResponse;
import org.example.service.ExternalAuthorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Listener для получения ответов от микросервиса авторов
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorResponseListener {

    private final ExternalAuthorService externalAuthorService;

    /**
     * Слушает топик author-response-topic и передает ответы в ExternalAuthorService
     * 
     * @param response ответ от микросервиса
     */
    @KafkaListener(
        topics = "${kafka.topics.author-response}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(AuthorResponse response) {
        log.info("=== Получено сообщение из Kafka ===");
        log.info("Request ID: {}", response.getRequestId());
        log.info("Success: {}", response.isSuccess());
        
        if (response.isSuccess() && response.getAuthors() != null) {
            log.info("Authors count: {}", response.getAuthors().size());
        } else if (!response.isSuccess()) {
            log.error("Error message: {}", response.getErrorMessage());
        }
        
        // Передаем ответ в сервис для обработки
        externalAuthorService.handleAuthorResponse(response);
    }
}
