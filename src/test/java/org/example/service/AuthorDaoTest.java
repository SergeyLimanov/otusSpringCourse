package org.example.service;

import org.example.dao.AuthorDao;
import org.example.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import(AuthorDao.class)
class AuthorDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private AuthorDao authorDao;

    @BeforeEach
    void setUp() {
        authorDao = new AuthorDao(jdbcTemplate);
    }

    @Test
    void shouldInsertAuthor() {
        Author author = authorDao.insert("Антон Чехов");
        assertThat(author.getId()).isNotNull();
        assertThat(author.getName()).isEqualTo("Антон Чехов");
    }

    @Test
    void shouldFindAuthorById() {
        Author inserted = authorDao.insert("Максим Горький");
        Author found = authorDao.findById(inserted.getId());
        assertThat(found).isEqualTo(inserted);
    }

    @Test
    void shouldFindAuthorByName() {
        authorDao.insert("Иван Тургенев");
        Author found = authorDao.findByName("Иван Тургенев");
        assertThat(found.getName()).isEqualTo("Иван Тургенев");
    }

    @Test
    void findByNameShouldThrowExceptionIfNotFound() {
        assertThatThrownBy(() -> authorDao.findByName("Неизвестный"))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }
}