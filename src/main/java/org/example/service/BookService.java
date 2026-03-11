package org.example.service;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;

import java.util.List;

public interface BookService {

    List<Book> findAll();
    Book createBook(String title, String authorName, String genreName);

    Book findById(Long id);
    void deleteById(Long id);
    Book updateBook(Long id, String title, String authorName, String genreName);
    List<Book> findBooksByAuthorName(String authorName);




}
