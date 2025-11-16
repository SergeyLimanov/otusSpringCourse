package org.example.service;

import org.example.dao.AuthorDao;
import org.example.dao.BookDao;
import org.example.dao.GenreDao;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDao bookDao;

    @Mock
    private AuthorDao authorDao;

    @Mock
    private GenreDao genreDao;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookDao, authorDao, genreDao);
    }

    @Test
    void shouldCreateBookWhenAuthorAndGenreExist() {
        // Arrange
        String title = "1984";
        String authorName = "Джордж Оруэлл";
        String genreName = "Антиутопия";

        Author author = new Author(1L, authorName);
        Genre genre = new Genre(2L, genreName);
        Book expectedBook = new Book(10L, title, author.getId(), genre.getId());

        when(authorDao.findByName(authorName)).thenReturn(author);
        when(genreDao.findByName(genreName)).thenReturn(genre);
        when(bookDao.insert(title, author.getId(), genre.getId())).thenReturn(expectedBook);

        // Act
        Book result = bookService.createBook(title, authorName, genreName);

        // Assert
        assertThat(result).isEqualTo(expectedBook);
        verify(authorDao).findByName(authorName);
        verify(genreDao).findByName(genreName);
        verify(bookDao).insert(title, author.getId(), genre.getId());
    }

    @Test
    void shouldReturnAllBooks() {
        // Arrange
        List<Book> books = List.of(
                new Book(1L, "Книга 1", 1L, 1L),
                new Book(2L, "Книга 2", 2L, 2L)
        );
        when(bookDao.findAll()).thenReturn(books);

        // Act
        List<Book> result = bookService.findAll();

        // Assert
        assertThat(result).isEqualTo(books);
        verify(bookDao).findAll();
    }
}