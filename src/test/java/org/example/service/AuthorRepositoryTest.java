package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.dao.AuthorRepository;
import org.example.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest

class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldSaveAndFindAuthorByName() {
        Author author = authorRepository.save(new Author(null, "Антон Чехов", new ArrayList<>()));

        Author found = authorRepository.findByName("Антон Чехов");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Антон Чехов");
    }

    @Test
    void shouldFindAllAuthors() {
        authorRepository.save(new Author(null, "Достоевский", new ArrayList<>()));
        authorRepository.save(new Author(null, "Толстой", new ArrayList<>()));

        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(5);
        assertThat(authors).extracting(Author::getName)
                .contains("Достоевский", "Толстой");
    }
}