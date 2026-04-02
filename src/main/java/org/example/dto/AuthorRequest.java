package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса авторов из Южной Америки через Kafka
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {
    /**
     * Уникальный идентификатор запроса для корреляции запроса и ответа
     */
    private String requestId;
    
    /**
     * Имя автора для поиска (опционально, если null - вернутся все авторы)
     */
    private String name;
}
