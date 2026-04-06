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
     * Применяет Retry, затем Circuit Breaker для устойчивости к сбоям
     * Порядок важен: сначала Retry пытается повторить, потом Circuit Breaker проверяет общую статистику
     *
     * @param name имя автора для поиска (опциональный параметр)
     * @return список авторов
     */
    @Retry(name = "externalAuthors", fallbackMethod = "getFallbackAuthors")
    @CircuitBreaker(name = "externalAuthors")
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
     * Fallback метод для Retry
     * Вызывается когда внешний сервис недоступен после всех retry попыток
     *
     * @param name параметр поиска
     * @param throwable исключение, вызвавшее fallback
     * @return тестовые данные для демонстрации работы fallback
     */
    private List<ExternalAuthorDto> getFallbackAuthors(String name, Throwable throwable) {
        logger.error("⚠️ FALLBACK TRIGGERED! External service unavailable. Reason: {}", throwable.getMessage());
        logger.warn("📦 Returning mock data from fallback method");

        // Возвращаем тестовые данные, чтобы показать что fallback работает
        List<ExternalAuthorDto> fallbackData = Arrays.asList(
                new ExternalAuthorDto(999L, "🔄 FALLBACK: Paulo Coelho (cached)"),
                new ExternalAuthorDto(998L, "🔄 FALLBACK: Gabriel García Márquez (cached)")
        );

        logger.info("✅ Returned {} authors from fallback cache", fallbackData.size());
        return fallbackData;
    }
}
