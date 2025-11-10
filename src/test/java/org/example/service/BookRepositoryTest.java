package org.example.service;

import org.example.dao.AuthorRepository;
import org.example.dao.BookRepository;
import org.example.dao.GenreRepository;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldSaveAndFindBookWithAuthorAndGenre() {
        Author author = authorRepository.save(new Author(null, "Джордж Оруэлл", new ArrayList<>()));
        Genre genre = genreRepository.save(new Genre(null, "Антиутопия", new ArrayList<>()));

        Book book = bookRepository.save(new Book(null, "1984", author, genre, new ArrayList<>()));

        Book found = bookRepository.findWithDetailsById(book.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("1984");
        assertThat(found.getAuthor().getName()).isEqualTo("Джордж Оруэлл");
        assertThat(found.getGenre().getName()).isEqualTo("Антиутопия");
    }

    @Test
    void shouldFindAllBooksWithAuthorAndGenre() {
        Author a = authorRepository.save(new Author(null, "Джоан Роулинг", new ArrayList<>()));
        Genre g = genreRepository.save(new Genre(null, "Фантастика", new ArrayList<>()));

        bookRepository.save(new Book(null, "Эркюль Пуаро", a, g, new ArrayList<>()));
        bookRepository.save(new Book(null, "Мисс Марпл", a, g, new ArrayList<>()));

        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(5);
        assertThat(books.get(4).getAuthor().getName()).isEqualTo("Джоан Роулинг");
    }

    @Test
    void shouldDeleteBookAndOrphanRemovalWorks() {
        Author author = authorRepository.save(new Author(null, "Тест", new ArrayList<>()));
        Genre genre = genreRepository.save(new Genre(null, "Тест", new ArrayList<>()));
        Book book = bookRepository.save(new Book(null, "Удалить", author, genre, new ArrayList<>()));

        bookRepository.deleteById(book.getId());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }
}