package org.example.service;

import org.example.dao.*;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final CommentRepository commentRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       GenreRepository genreRepository, CommentRepository commentRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book createBook(String title, String authorName, String genreName) {
        Author author = authorRepository.findByName(authorName);
        if (author == null) {
            author = authorRepository.save(new Author(null, authorName, new ArrayList<>()));
        }
        Genre genre = genreRepository.findByName(genreName);
        if (genre == null) {
            genre = genreRepository.save(new Genre(null, genreName, new ArrayList<>()));
        }
        return bookRepository.save(new Book(null, title, author, genre, new ArrayList<>()));
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findWithDetailsById(id).orElse(null);
    }

    public void deleteById(Long id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    public Book updateBook(Long id, String title, String authorName, String genreName) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));

        Author author = authorRepository.findByName(authorName);
        if (author == null) {
            author = authorRepository.save(new Author(null, authorName, new ArrayList<>()));
        }

        Genre genre = genreRepository.findByName(genreName);
        if (genre == null) {
            genre = genreRepository.save(new Genre(null, genreName, new ArrayList<>()));
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Comment> findCommentsByBookId(Long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    public Comment addComment(Long bookId, String text) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + bookId));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setBook(book);

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NoSuchElementException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    public Author createAuthor(String name) {
        return authorRepository.save(new Author(null, name, new ArrayList<>()));
    }

    @Transactional(readOnly = true)
    public List<Author> listAllAuthors() {
        return authorRepository.findAll();
    }

    public Genre createGenre(String name) {
        return genreRepository.save(new Genre(null, name, new ArrayList<>()));
    }

    @Transactional(readOnly = true)
    public List<Genre> listAllGenres() {
        return genreRepository.findAll();
    }
}