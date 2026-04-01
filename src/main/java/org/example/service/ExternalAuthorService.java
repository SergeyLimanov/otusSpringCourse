package org.example.service;

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
     *
     * @param name имя автора для поиска (опциональный параметр)
     * @return список авторов
     */
    public List<ExternalAuthorDto> getSouthAmericaAuthors(String name) {
        try {
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
        } catch (RestClientException e) {
            logger.error("Error calling external author service: {}", e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Unexpected error while fetching external authors: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
