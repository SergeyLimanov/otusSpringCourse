package org.example.dao;

import org.example.model.Book;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class BookDao {
    private final NamedParameterJdbcTemplate jdbc;

    public BookDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Book> findAll() {
        return jdbc.query("SELECT id, title, author_id, genre_id FROM books",
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("author_id"),
                        rs.getLong("genre_id")
                ));
    }

    public Book insert(String title, Long authorId, Long genreId) {
        String sql = """
            INSERT INTO books (title, author_id, genre_id)
            VALUES (:title, :authorId, :genreId)
            """;
        Map<String, Object> params = Map.of(
                "title", title,
                "authorId", authorId,
                "genreId", genreId
        );
        jdbc.update(sql, params);

        // Возвращаем вставленную книгу (по последнему ID или по уникальному полю)
        return findByTitle(title);
    }

    public Book findById(Long id) {
        return jdbc.queryForObject("SELECT id, title, author_id, genre_id FROM books WHERE id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("author_id"),
                        rs.getLong("genre_id")
                ));
    }

    public Book findByTitle(String title) {
        return jdbc.queryForObject("SELECT id, title, author_id, genre_id FROM books WHERE title = :title",
                Map.of("title", title),
                (rs, rowNum) -> new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("author_id"),
                        rs.getLong("genre_id")
                ));
    }

    public void deleteById(Long id) {
        jdbc.update("DELETE FROM books WHERE id = :id", Map.of("id", id));
    }

    public void update(Long id, String title, Long authorId, Long genreId) {
        String sql = """
            UPDATE books
            SET title = :title, author_id = :authorId, genre_id = :genreId
            WHERE id = :id
            """;
        Map<String, Object> params = Map.of(
                "id", id,
                "title", title,
                "authorId", authorId,
                "genreId", genreId
        );
        jdbc.update(sql, params);
    }
}
