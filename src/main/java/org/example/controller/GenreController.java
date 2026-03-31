package org.example.controller;

import org.example.dto.GenreDto;
import org.example.mapper.DtoMapper;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;
    private final DtoMapper mapper;

    public GenreController(GenreService genreService, DtoMapper mapper) {
        this.genreService = genreService;
        this.mapper = mapper;
    }

    /**
     * GET /api/genres - получить все жанры
     */
    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        List<Genre> genres = genreService.listAllGenres();
        return ResponseEntity.ok(mapper.toGenreDtoList(genres));
    }

    /**
     * POST /api/genres - создать новый жанр
     */
    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        Genre genre = genreService.createGenre(genreDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toGenreDto(genre));
    }
}
