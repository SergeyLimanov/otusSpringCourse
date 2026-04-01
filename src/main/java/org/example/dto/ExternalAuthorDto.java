package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для авторов из внешнего микросервиса (South America Authors Service)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAuthorDto {
    private Long id;
    private String name;
}
