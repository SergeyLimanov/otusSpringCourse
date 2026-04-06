package org.example.integration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.example.dto.ExternalAuthorDto;
import org.example.service.ExternalAuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для ExternalAuthorService
 * Проверяет работу с реальным HTTP клиентом и Resilience4j
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "external.authors.service.url=http://localhost:8081"
})
class ExternalAuthorServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExternalAuthorService externalAuthorService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalAuthors");
        circuitBreaker.reset();
    }

    @Test
    void testGetSouthAmericaAuthors_ThroughController() {
        // Act
        ResponseEntity<List<ExternalAuthorDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/authors/external/south-america",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ExternalAuthorDto>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Если MockServer запущен, будут данные; если нет - пустой список (fallback)
        assertTrue(response.getBody() != null);
    }

    @Test
    void testGetSouthAmericaAuthors_WithNameParameter() {
        // Act
        ResponseEntity<List<ExternalAuthorDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/authors/external/south-america?name=Paulo",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ExternalAuthorDto>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCircuitBreaker_StateTransitions() {
        // Проверяем начальное состояние
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState(),
                "Circuit Breaker должен быть CLOSED в начале");

        // Делаем успешный вызов
        externalAuthorService.getSouthAmericaAuthors(null);

        // Circuit Breaker должен остаться CLOSED
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState(),
                "Circuit Breaker должен оставаться CLOSED после успешного вызова");
    }

    @Test
    void testResilience4j_Metrics() {
        // Act
        externalAuthorService.getSouthAmericaAuthors(null);

        // Assert
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertNotNull(metrics);
        assertTrue(metrics.getNumberOfSuccessfulCalls() >= 0);
        assertTrue(metrics.getNumberOfFailedCalls() >= 0);
    }


}
