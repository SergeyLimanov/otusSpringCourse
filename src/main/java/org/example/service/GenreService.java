package org.example.service;

import org.example.model.Author;
import org.example.model.Genre;

import java.util.List;

public interface GenreService {

    Genre createGenre(String name);
    List<Genre> listAllGenres();

    Genre findByName(String name);
    Genre save(Genre genre);

}
