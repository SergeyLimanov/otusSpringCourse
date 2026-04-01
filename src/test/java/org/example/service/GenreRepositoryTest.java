package org.example.service;

import org.example.dao.GenreRepository;
import org.example.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldSaveAndFindGenreByName() {
        Genre genre = genreRepository.save(new Genre(null, "Фантастика", new ArrayList<>()));

        Genre found = genreRepository.findByName("Фантастика");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Фантастика");
    }

    @Test
    void shouldFindAllGenres() {
        genreRepository.save(new Genre(null, "Мистика", new ArrayList<>()));
        genreRepository.save(new Genre(null, "Мультфильмы", new ArrayList<>()));

        List<Genre> genres = genreRepository.findAll();
        assertThat(genres).hasSize(2);
        assertThat(genres).extracting(Genre::getName)
                .contains("Мистика", "Мультфильмы");
    }
}