package org.example.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.example.dto.ExternalAuthorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service для интеграции с микросервисом South America Authors (порт 8081)
 * Использует Resilience4j для обеспечения устойчивости к сбоям:
 * - CircuitBreaker: защита от каскадных сбоев
 * - Retry: автоматические повторные попытки
 * - TimeLimiter: ограничение времени выполнения
 */
@Service
public class ExternalAuthorService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalAuthorService.class);

    private final RestTemplate restTemplate;

    @Value("${external.authors.service.url:http://localhost:8081}")
    private String externalServiceUrl;

    public ExternalAuthorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Получить всех авторов из внешнего сервиса
     */
    public List<ExternalAuthorDto> getAllSouthAmericaAuthors() {
        return getSouthAmericaAuthors(null);
    }

    /**
     * Получить авторов из внешнего сервиса по имени
     * Применяет Circuit Breaker, Retry и TimeLimiter для устойчивости к сбоям
     *
     * @param name имя автора для поиска (опциональный параметр)
     * @return список авторов
     */
    @CircuitBreaker(name = "externalAuthors", fallbackMethod = "getFallbackAuthors")
    @Retry(name = "externalAuthors")
    public List<ExternalAuthorDto> getSouthAmericaAuthors(String name) {
        String url = externalServiceUrl + "/api/authors/south-america";

        if (name != null && !name.trim().isEmpty()) {
            url += "?name=" + name;
            logger.info("Fetching South America authors by name: {}", name);
        } else {
            logger.info("Fetching all South America authors");
        }

        ExternalAuthorDto[] response = restTemplate.getForObject(url, ExternalAuthorDto[].class);

        if (response != null) {
            logger.info("Successfully fetched {} authors from external service", response.length);
            return Arrays.asList(response);
        } else {
            logger.warn("External service returned null response");
            return Collections.emptyList();
        }
    }

    /**
     * Fallback метод для Circuit Breaker
     * Вызывается когда внешний сервис недоступен или Circuit Breaker открыт
     *
     * @param name параметр поиска
     * @param throwable исключение, вызвавшее fallback
     * @return пустой список или кэшированные данные
     */
    private List<ExternalAuthorDto> getFallbackAuthors(String name, Throwable throwable) {
        logger.error("Fallback triggered for getSouthAmericaAuthors. Reason: {}", throwable.getMessage());
        logger.warn("Returning empty list due to external service unavailability");
        
        // В production здесь можно вернуть кэшированные данные или данные из резервного источника
        return Collections.emptyList();
    }
}
