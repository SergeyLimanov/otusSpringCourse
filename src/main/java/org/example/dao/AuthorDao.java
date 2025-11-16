package org.example.dao;

import org.example.model.Author;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AuthorDao {
    private final NamedParameterJdbcTemplate jdbc;

    public AuthorDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Author> findAll() {
        return jdbc.query("SELECT id, name FROM authors", (rs, rowNum) ->
                new Author(rs.getLong("id"), rs.getString("name")));
    }

    public Author insert(String name) {
        String sql = "INSERT INTO authors (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", name);
        jdbc.update(sql, params);

        return findByName(name);
    }

    public Author findById(Long id) {
        return jdbc.queryForObject("SELECT id, name FROM authors WHERE id = :id",
                Map.of("id", id),
                (rs, rowNum) -> new Author(rs.getLong("id"), rs.getString("name")));
    }

    public Author findByName(String name) {
        return jdbc.queryForObject("SELECT id, name FROM authors WHERE name = :name",
                Map.of("name", name),
                (rs, rowNum) -> new Author(rs.getLong("id"), rs.getString("name")));
    }
}