package org.example.service;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@ShellComponent
public class LibraryCommands {

    private final BookServiceImpl bookServiceImpl;
    private final AuthorServiceImpl authorService;
    private final CommentServiceImpl commentService;
    private final GenreService genreService;

    public LibraryCommands(BookServiceImpl bookServiceImpl, AuthorServiceImpl authorService, CommentServiceImpl commentService, GenreService genreService) {
        this.bookServiceImpl = bookServiceImpl;
        this.authorService = authorService;
        this.commentService = commentService;
        this.genreService = genreService;
    }

    @ShellMethod("Test command")
    public String hello() {
        return "Hello!";
    }

    // === Книги ===

    @ShellMethod(key = "books", value = "List all books in the library")
    public List<String> listBooks() {
        List<Book> books = bookServiceImpl.findAll();
        List<String> result = new ArrayList<>();
        for (Book book : books) {
            String line = String.format(
                    "Book[id=%d, title='%s', author='%s', genre='%s']",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor() != null ? book.getAuthor().getName() : "N/A",
                    book.getGenre() != null ? book.getGenre().getName() : "N/A"
            );
            result.add(line);
        }
        return result;
    }

    @ShellMethod(key = "book-add", value = "Add a new book")
    public String addBook(
            @ShellOption(help = "Book title") String title,
            @ShellOption(help = "Author name") String author,
            @ShellOption(help = "Genre name") String genre) {
        try {
            Book book = bookServiceImpl.createBook(title, author, genre);
            return String.format(
                    "Book added: id=%d, title='%s', author='%s', genre='%s'",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor().getName(),
                    book.getGenre().getName()
            );
        } catch (NoSuchElementException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "book-update", value = "Update a book")
    public String updateBook(
            @ShellOption(help = "Book ID") Long id,
            @ShellOption(help = "New title") String title,
            @ShellOption(help = "Author name") String author,
            @ShellOption(help = "Genre name") String genre) {
        try {
            Book updated = bookServiceImpl.updateBook(id, title, author, genre);
            return String.format(
                    "Book updated: id=%d, title='%s', author='%s', genre='%s'",
                    updated.getId(),
                    updated.getTitle(),
                    updated.getAuthor().getName(),
                    updated.getGenre().getName()
            );
        } catch (NoSuchElementException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "book-delete", value = "Delete a book by ID")
    public String deleteBook(@ShellOption(help = "Book ID") Long id) {
        try {
            bookServiceImpl.deleteById(id);
            return "Book with ID " + id + " deleted.";
        } catch (Exception e) {
            return "Error deleting book: " + e.getMessage();
        }
    }

    @ShellMethod(key = "book-get", value = "Get book with comments")
    public String getBook(@ShellOption(help = "Book ID") Long id) {
        Book book = bookServiceImpl.findById(id);
        if (book == null) {
            return "Book not found with id: " + id;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "Book[id=%d, title='%s', author='%s', genre='%s']%n",
                book.getId(),
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor().getName() : "N/A",
                book.getGenre() != null ? book.getGenre().getName() : "N/A"
        ));
        List<Comment> comments = commentService.findCommentsByBookId(id);
        sb.append("Comments (").append(comments.size()).append("):").append(System.lineSeparator());
        for (Comment c : comments) {
            sb.append(String.format("  [%d] %s%n", c.getId(), c.getText()));
        }
        return sb.toString();
    }

    // === Авторы ===

    @ShellMethod(key = "authors", value = "List all authors")
    public List<Author> listAuthors() {
        return authorService.listAllAuthors();
    }

    @ShellMethod(key = "author-add", value = "Add a new author")
    public String addAuthor(@ShellOption(help = "Author name") String name) {
        try {
            Author author = authorService.createAuthor(name);
            return "Author added: " + author;
        } catch (Exception e) {
            return "Error adding author: " + e.getMessage();
        }
    }

    // === Жанры ===

    @ShellMethod(key = "genres", value = "List all genres")
    public List<Genre> listGenres() {
        return genreService.listAllGenres();
    }

    @ShellMethod(key = "genre-add", value = "Add a new genre")
    public String addGenre(@ShellOption(help = "Genre name") String name) {
        try {
            Genre genre = genreService.createGenre(name);
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
            Comment comment = commentService.addComment(bookId, text);
            return "Comment added (ID: " + comment.getId() + "): " + text;
        } catch (NoSuchElementException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "comments", value = "List all comments for a book")
    public List<Comment> listComments(@ShellOption(help = "Book ID") Long bookId) {
        return commentService.findCommentsByBookId(bookId);
    }

    @ShellMethod(key = "comment-delete", value = "Delete a comment by ID")
    public String deleteComment(@ShellOption(help = "Comment ID") Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return "Comment with ID " + commentId + " deleted.";
        } catch (Exception e) {
            return "Error deleting comment: " + e.getMessage();
        }
    }

    @ShellMethod(key = "comment-update", value = "Update a comment by ID")
    public String updateComment(
            @ShellOption(help = "Comment ID") Long commentId,
            @ShellOption(help = "New comment text") String text) {
        try {
            Comment updated = commentService.updateComment(commentId, text);
            return "Comment updated (ID: " + updated.getId() + "): " + updated.getText();
        } catch (NoSuchElementException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @ShellMethod(key = "books-by-author", value = "Find books by author name")
    public List<Book> booksByAuthor(@ShellOption String authorName) {
        return bookServiceImpl.findBooksByAuthorName(authorName);
    }
}