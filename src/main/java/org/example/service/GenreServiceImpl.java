package org.example.service;

import org.example.dao.GenreRepository;
import org.example.model.Genre;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreServiceImpl implements GenreService{
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional
    @Override
    public Genre createGenre(String name) {
        return genreRepository.save(new Genre(null, name, new ArrayList<>()));
    }

    @Override
    public List<Genre> listAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findByName(String name) {
        return genreRepository.findByName(name);
    }

    @Transactional
    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }
}
