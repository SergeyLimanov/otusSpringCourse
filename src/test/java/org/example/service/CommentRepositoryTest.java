package org.example.service;

import org.example.dao.AuthorRepository;
import org.example.dao.BookRepository;
import org.example.dao.CommentRepository;
import org.example.dao.GenreRepository;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void shouldSaveAndFindCommentsByBook() {
        Author author = authorRepository.save(new Author(null, "Автор", new ArrayList<>()));
        Genre genre = genreRepository.save(new Genre(null, "Жанр", new ArrayList<>()));
        Book book = bookRepository.save(new Book(null, "Книга", author, genre, new ArrayList<>()));

        Comment c1 = commentRepository.save(new Comment(null, "Отлично!", book));
        Comment c2 = commentRepository.save(new Comment(null, "Рекомендую", book));

        List<Comment> comments = commentRepository.findAllByBookId(book.getId());
        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText)
                .contains("Отлично!", "Рекомендую");
    }

    @Test
    void shouldDeleteCommentsByBookId() {
        // Подготовка
        Author a = authorRepository.save(new Author(null, "A", new ArrayList<>()));
        Genre g = genreRepository.save(new Genre(null, "G", new ArrayList<>()));
        Book b1 = bookRepository.save(new Book(null, "Книга 1", a, g, new ArrayList<>()));
        Book b2 = bookRepository.save(new Book(null, "Книга 2", a, g, new ArrayList<>()));

        commentRepository.save(new Comment(null, "К1", b1));
        commentRepository.save(new Comment(null, "К2", b2));

        // Удаляем комментарии только к книге b1
        commentRepository.deleteByBookId(b1.getId());

        List<Comment> all = (List<Comment>) commentRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getText()).isEqualTo("К2");
    }

    @Test
    void shouldFindCommentsContainingKeyword() {
        Author a = authorRepository.save(new Author(null, "A", new ArrayList<>()));
        Genre g = genreRepository.save(new Genre(null, "G", new ArrayList<>()));
        Book b = bookRepository.save(new Book(null, "Книга", a, g, new ArrayList<>()));

        commentRepository.save(new Comment(null, "Это потрясающая книга!", b));
        commentRepository.save(new Comment(null, "Скучно и нудно", b));
        commentRepository.save(new Comment(null, "Рекомендую всем", b));

        List<Comment> comments = commentRepository.findByTextContaining("книга");
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getText()).containsIgnoringCase("книга");
    }
}