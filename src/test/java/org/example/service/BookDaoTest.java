package org.example.service;

import org.example.dao.AuthorDao;
import org.example.dao.BookDao;
import org.example.dao.GenreDao;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import({BookDao.class, AuthorDao.class, GenreDao.class})
class BookDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private BookDao bookDao;
    private AuthorDao authorDao;
    private GenreDao genreDao;

    @BeforeEach
    void setUp() {
        authorDao = new AuthorDao(jdbcTemplate);
        genreDao = new GenreDao(jdbcTemplate);
        bookDao = new BookDao(jdbcTemplate);
    }

    @Test
    void shouldInsertAndFindBook() {
        Author author = authorDao.insert("Джордж Оруэлл");
        Genre genre = genreDao.insert("Антиутопия");

        Book book = bookDao.insert("1984", author.getId(), genre.getId());
        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("1984");

        Book found = bookDao.findById(book.getId());
        assertThat(found).isEqualTo(book);
    }

    @Test
    void shouldDeleteBook() {
        Author a = authorDao.insert("Рэй Брэдбери");
        Genre g = genreDao.insert("Фантастика");
        Book book = bookDao.insert("451 градус по Фаренгейту", a.getId(), g.getId());

        bookDao.deleteById(book.getId());
        assertThatThrownBy(() -> bookDao.findById(book.getId()))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }
}