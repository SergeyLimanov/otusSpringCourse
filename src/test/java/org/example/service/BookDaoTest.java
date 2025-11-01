package org.example.service;

import org.example.dao.BookDao;
import org.example.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class BookDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private BookDao bookDao;

    @BeforeEach
    void setUp() {
        bookDao = new BookDao(jdbc);
        // Вставляем тестовые данные
        jdbc.update("INSERT INTO authors (name) VALUES ('Test Author')", Map.of());
        jdbc.update("INSERT INTO genres (name) VALUES ('Test Genre')", Map.of());
    }

    @Test
    void shouldInsertAndFindBook() {
        Book book = bookDao.insert("Test Book", 1L, 1L);
        assertThat(book.getTitle()).isEqualTo("Test Book");

        Book found = bookDao.findById(book.getId());
        assertThat(found).isEqualTo(book);
    }
}