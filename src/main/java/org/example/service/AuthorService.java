package org.example.service;

import org.example.model.Author;

import java.util.List;

public interface AuthorService {

    Author createAuthor(String name);
    List<Author> listAllAuthors();
    Author findByName(String name);

    Author save(Author author);
}
