package org.example.service;

import org.example.dao.AuthorRepository;
import org.example.dao.BookRepository;
import org.example.dao.CommentRepository;
import org.example.dao.GenreRepository;
import org.example.model.Author;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class AuthorServiceImpl implements AuthorService{
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    @Override
    public Author createAuthor(String name) {
        return authorRepository.save(new Author(null, name, new ArrayList<>()));
    }

    @Override
    public List<Author> listAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public Author findByName(String name) {
       return authorRepository.findByName(name);
    }

    @Transactional
    @Override
    public Author save(Author author) {
        return authorRepository.save(author);
    }
}
