package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для ответа с авторами из Южной Америки через Kafka
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponse {
    /**
     * Уникальный идентификатор запроса для корреляции запроса и ответа
     */
    private String requestId;
    
    /**
     * Список найденных авторов
     */
    private List<ExternalAuthorDto> authors;
    
    /**
     * Флаг успешности выполнения запроса
     */
    private boolean success;
    
    /**
     * Сообщение об ошибке (если success = false)
     */
    private String errorMessage;
    
    /**
     * Конструктор для успешного ответа
     */
    public static AuthorResponse success(String requestId, List<ExternalAuthorDto> authors) {
        return new AuthorResponse(requestId, authors, true, null);
    }
    
    /**
     * Конструктор для ответа с ошибкой
     */
    public static AuthorResponse error(String requestId, String errorMessage) {
        return new AuthorResponse(requestId, null, false, errorMessage);
    }
}
