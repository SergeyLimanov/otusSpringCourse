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
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final CommentRepository commentRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           AuthorService authorService,
                           GenreService genreService,
                           CommentRepository commentRepository) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
        this.commentRepository = commentRepository;
    }

    // --- КНИГИ ---

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public Book createBook(String title, String authorName, String genreName) {
        Author author = authorService.findByName(authorName);
        if (author == null) {
            author = authorService.save(new Author(null, authorName, new ArrayList<>()));
        }

        Genre genre = genreService.findByName(genreName);
        if (genre == null) {
            genre = genreService.save(new Genre(null, genreName, new ArrayList<>()));
        }

        return bookRepository.save(new Book(null, title, author, genre, new ArrayList<>()));
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findWithDetailsById(id).orElse(null);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Book updateBook(Long id, String title, String authorName, String genreName) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + id));

        Author author = authorService.findByName(authorName);
        if (author == null) {
            author = authorService.save(new Author(null, authorName, new ArrayList<>()));
        }

        Genre genre = genreService.findByName(genreName);
        if (genre == null) {
            genre = genreService.save(new Genre(null, genreName, new ArrayList<>()));
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);

        return bookRepository.save(book);
    }

    @Override
    // --- Доп. статистика по книгам (через репозиторий книг) ---
    public List<Book> findBooksByAuthorName(String authorName) {
        Author author = authorService.findByName(authorName);
        if (author == null) {
            return List.of();
        }
        return bookRepository.findAllByAuthorId(author.getId());
    }

}