package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.dao.AuthorRepository;
import org.example.dao.BookRepository;
import org.example.dao.GenreRepository;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
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
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

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
        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getName)
                .contains("Достоевский", "Толстой");
    }

    @Test
    void shouldFindAuthorsWithMoreThanNBooks() {
        Author a1 = new Author(null, "Плодовитый", new ArrayList<>());
        Author a2 = new Author(null, "Однокнижник", new ArrayList<>());
        authorRepository.save(a1);
        authorRepository.save(a2);

        Genre g = genreRepository.save(new Genre(null, "Разное", new ArrayList<>()));

        // Добавим 3 книги первому автору
        for (int i = 1; i <= 3; i++) {
            bookRepository.save(new Book(null, "Книга " + i, a1, g, new ArrayList<>()));
        }
        // И 1 книгу второму
        bookRepository.save(new Book(null, "Единственная", a2, g, new ArrayList<>()));

        // Обновим связь (в реальности CascadeType.ALL и orphanRemoval помогают, но в тесте проще пересохранить)
        // На практике, если связи двунаправленные, нужно установить book.setAuthor(author) — у вас это есть.

        List<Author> authors = authorRepository.findAuthorsWithMoreThanNBooks(2);
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getName()).isEqualTo("Плодовитый");
    }
}