package org.example.service;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.NoSuchElementException;

@ShellComponent
public class LibraryCommands {

    private final BookService bookService;

    public LibraryCommands(BookService bookService) {
        this.bookService = bookService;
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

    @ShellMethod(key = "book-get", value = "Get book with comments")
    public Book getBook(@ShellOption(help = "Book ID") Long id) {
        try {
            return bookService.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Book not found: " + e.getMessage());
        }
    }

    // === Авторы ===

    @ShellMethod(key = "authors", value = "List all authors")
    public List<Author> listAuthors() {
        return bookService.listAllAuthors();
    }

    @ShellMethod(key = "author-add", value = "Add a new author")
    public String addAuthor(@ShellOption(help = "Author name") String name) {
        try {
            Author author = bookService.createAuthor(name);
            return "Author added: " + author;
        } catch (Exception e) {
            return "Error adding author: " + e.getMessage();
        }
    }

    // === Жанры ===

    @ShellMethod(key = "genres", value = "List all genres")
    public List<Genre> listGenres() {
        return bookService.listAllGenres();
    }

    @ShellMethod(key = "genre-add", value = "Add a new genre")
    public String addGenre(@ShellOption(help = "Genre name") String name) {
        try {
            Genre genre = bookService.createGenre(name);
            return "Genre added: " + genre;
        } catch (Exception e) {
            return "Error adding genre: " + e.getMessage();
        }
    }

    // === Комментарии ===

    @ShellMethod(key = "comment-add", value = "Add a comment to a book")
    public String addComment(
            @ShellOption(help = "Book ID") Long bookId,
            @ShellOption(help = "Comment text") String text) {
        try {
            Comment comment = bookService.addComment(bookId, text);
            return "Comment added (ID: " + comment.getId() + "): " + text;
        } catch (NoSuchElementException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "comments", value = "List all comments for a book")
    public List<Comment> listComments(@ShellOption(help = "Book ID") Long bookId) {
        return bookService.findCommentsByBookId(bookId);
    }

    @ShellMethod(key = "comment-delete", value = "Delete a comment by ID")
    public String deleteComment(@ShellOption(help = "Comment ID") Long commentId) {
        try {
            bookService.deleteComment(commentId);
            return "Comment with ID " + commentId + " deleted.";
        } catch (Exception e) {
            return "Error deleting comment: " + e.getMessage();
        }
    }
}