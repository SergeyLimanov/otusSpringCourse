package org.example.dao;

import org.example.model.Genre;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class GenreDao {

    private final NamedParameterJdbcTemplate jdbc;

    public GenreDao(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Genre> findAll() {
        return jdbc.query(
                "SELECT id, name FROM genres",
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }
        );
    }

    public Genre insert(String name) {
        String sql = "INSERT INTO genres (name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource("name", name);
        jdbc.update(sql, params);

        return findByName(name);
    }

    public Genre findById(Long id) {
        return jdbc.queryForObject(
                "SELECT id, name FROM genres WHERE id = :id",
                Map.of("id", id),
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }
        );
    }

    public Genre findByName(String name) {
        return jdbc.queryForObject(
                "SELECT id, name FROM genres WHERE name = :name",
                Map.of("name", name),
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }
        );
    }
}
