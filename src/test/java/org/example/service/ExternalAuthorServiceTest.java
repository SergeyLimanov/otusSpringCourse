package org.example.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.example.dto.ExternalAuthorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Тесты для ExternalAuthorService с проверкой Resilience4j паттернов
 */
@SpringBootTest
class ExternalAuthorServiceTest {

    @Autowired
    private ExternalAuthorService externalAuthorService;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        // Получаем Circuit Breaker и сбрасываем его состояние перед каждым тестом
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("externalAuthors");
        circuitBreaker.reset();
    }

    @Test
    void testGetSouthAmericaAuthors_Success() {
        // Arrange
        ExternalAuthorDto[] mockResponse = {
                new ExternalAuthorDto(1L, "Paulo Coelho"),
                new ExternalAuthorDto(2L, "Gabriel García Márquez")
        };
        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        // Act
        List<ExternalAuthorDto> result = externalAuthorService.getSouthAmericaAuthors(null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Paulo Coelho", result.get(0).getName());
        assertEquals("Gabriel García Márquez", result.get(1).getName());
        verify(restTemplate, times(1)).getForObject(anyString(), any());
    }

    @Test
    void testGetSouthAmericaAuthors_WithNameParameter() {
        // Arrange
        ExternalAuthorDto[] mockResponse = {
                new ExternalAuthorDto(1L, "Paulo Coelho")
        };
        when(restTemplate.getForObject(contains("name=Paulo"), any())).thenReturn(mockResponse);

        // Act
        List<ExternalAuthorDto> result = externalAuthorService.getSouthAmericaAuthors("Paulo");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paulo Coelho", result.get(0).getName());
        verify(restTemplate, times(1)).getForObject(contains("name=Paulo"), any());
    }

    @Test
    void testGetSouthAmericaAuthors_NullResponse() {
        // Arrange
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);

        // Act
        List<ExternalAuthorDto> result = externalAuthorService.getSouthAmericaAuthors(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSouthAmericaAuthors_RestClientException_TriggersRetry() {
        // Arrange
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(new RestClientException("Connection refused"))
                .thenThrow(new RestClientException("Connection refused"))
                .thenReturn(new ExternalAuthorDto[]{new ExternalAuthorDto(1L, "Paulo Coelho")});

        // Act
        List<ExternalAuthorDto> result = externalAuthorService.getSouthAmericaAuthors(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        // Retry сработал: 2 неудачные попытки + 1 успешная = 3 вызова
        verify(restTemplate, times(3)).getForObject(anyString(), any());
    }

    @Test
    void testGetSouthAmericaAuthors_AllRetriesFail_TriggersFallback() {
        // Arrange
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(new RestClientException("Connection refused"));

        // Act
        List<ExternalAuthorDto> result = externalAuthorService.getSouthAmericaAuthors(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Fallback должен вернуть пустой список");
        // Retry делает 3 попытки
        verify(restTemplate, times(3)).getForObject(anyString(), any());
    }

    @Test
    void testCircuitBreaker_OpensAfterFailureThreshold() {
        // Arrange
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(new RestClientException("Service unavailable"));

        // Act - делаем минимум 5 вызовов для срабатывания Circuit Breaker
        for (int i = 0; i < 6; i++) {
            externalAuthorService.getSouthAmericaAuthors(null);
        }

        // Assert
        CircuitBreaker.State state = circuitBreaker.getState();
        assertTrue(state == CircuitBreaker.State.OPEN || state == CircuitBreaker.State.HALF_OPEN,
                "Circuit Breaker должен быть OPEN или HALF_OPEN после множественных ошибок");
    }

    @Test
    void testCircuitBreaker_RemainsClosedOnSuccess() {
        // Arrange
        ExternalAuthorDto[] mockResponse = {new ExternalAuthorDto(1L, "Paulo Coelho")};
        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        // Act
        for (int i = 0; i < 10; i++) {
            externalAuthorService.getSouthAmericaAuthors(null);
        }

        // Assert
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState(),
                "Circuit Breaker должен оставаться CLOSED при успешных вызовах");
    }

    @Test
    void testGetAllSouthAmericaAuthors() {
        // Arrange
        ExternalAuthorDto[] mockResponse = {
                new ExternalAuthorDto(1L, "Paulo Coelho"),
                new ExternalAuthorDto(2L, "Pablo Neruda")
        };
        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        // Act
        List<ExternalAuthorDto> result = externalAuthorService.getAllSouthAmericaAuthors();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(1)).getForObject(anyString(), any());
    }

    @Test
    void testCircuitBreaker_Metrics() {
        // Arrange
        ExternalAuthorDto[] mockResponse = {new ExternalAuthorDto(1L, "Paulo Coelho")};
        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        // Act
        externalAuthorService.getSouthAmericaAuthors(null);

        // Assert
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertEquals(1, metrics.getNumberOfSuccessfulCalls(),
                "Должен быть 1 успешный вызов");
        assertEquals(0, metrics.getNumberOfFailedCalls(),
                "Не должно быть неудачных вызовов");
    }
}
