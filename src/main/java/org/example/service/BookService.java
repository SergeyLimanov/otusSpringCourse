package org.example.service;

import org.example.dao.AuthorDao;
import org.example.dao.BookDao;
import org.example.dao.GenreDao;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Genre;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookService(BookDao bookDao, AuthorDao authorDao, GenreDao genreDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    public List<Book> findAll() {
        return bookDao.findAll();
    }

    public Book createBook(String title, String authorName, String genreName) {
        Author author = authorDao.findByName(authorName);
        if (author == null) {
            author = authorDao.insert(authorName); // создаём, если нет
        }
        Genre genre = genreDao.findByName(genreName);
        if (genre == null) {
            genre = genreDao.insert(genreName);
        }
        return bookDao.insert(title, author.getId(), genre.getId());
    }

    public Book findById(Long id) {
        return bookDao.findById(id);
    }

    public void deleteById(Long id) {
        bookDao.deleteById(id);
    }

    public Book updateBook(Long id, String title, String authorName, String genreName) {
        Author author = authorDao.findByName(authorName);
        if (author == null) {
            throw new NoSuchElementException("Author not found: " + authorName);
        }
        Genre genre = genreDao.findByName(genreName);
        if (genre == null) {
            throw new NoSuchElementException("Genre not found: " + genreName);
        }
        bookDao.update(id, title, author.getId(), genre.getId());
        return bookDao.findById(id);
    }
}