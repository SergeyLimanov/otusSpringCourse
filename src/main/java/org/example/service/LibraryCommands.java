package org.example.service;

import org.example.dao.AuthorDao;
import org.example.dao.GenreDao;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.NoSuchElementException;

@ShellComponent
public class LibraryCommands {

    private final BookService bookService;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public LibraryCommands(BookService bookService, AuthorDao authorDao, GenreDao genreDao) {
        this.bookService = bookService;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @ShellMethod("Test command")
    public String hello() {
        return "Hello!";
    }

    // === Книги ===

    @ShellMethod(key = "books", value = "List all books in the library")
    public List<Book> listBooks() {
        return bookService.findAll();
    }

    @ShellMethod(key = "book-add", value = "Add a new book")
    public String addBook(
            @ShellOption(help = "Book title") String title,
            @ShellOption(help = "Author name") String author,
            @ShellOption(help = "Genre name") String genre) {
        try {
            Book book = bookService.createBook(title, author, genre);
            return "Book added: " + book;
        } catch (NoSuchElementException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "book-delete", value = "Delete a book by ID")
    public String deleteBook(@ShellOption(help = "Book ID") Long id) {
        try {
            bookService.deleteById(id);
            return "Book with ID " + id + " deleted.";
        } catch (Exception e) {
            return "Error deleting book: " + e.getMessage();
        }
    }

    // === Авторы ===

    @ShellMethod(key = "authors", value = "List all authors")
    public List<Author> listAuthors() {
        return authorDao.findAll();
    }

    @ShellMethod(key = "author-add", value = "Add a new author")
    public String addAuthor(@ShellOption(help = "Author name") String name) {
        try {
            Author author = authorDao.insert(name);
            return "Author added: " + author;
        } catch (Exception e) {
            return "Error adding author: " + e.getMessage();
        }
    }

    // === Жанры ===

    @ShellMethod(key = "genres", value = "List all genres")
    public List<Genre> listGenres() {
        return genreDao.findAll();
    }

    @ShellMethod(key = "genre-add", value = "Add a new genre")
    public String addGenre(@ShellOption(help = "Genre name") String name) {
        try {
            Genre genre = genreDao.insert(name);
            return "Genre added: " + genre;
        } catch (Exception e) {
            return "Error adding genre: " + e.getMessage();
        }
    }
}