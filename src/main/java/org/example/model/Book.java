package org.example.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Book {
    private Long id;
    private String title;
    private Long authorId;
    private Long genreId;
}
