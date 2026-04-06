package org.example.controller;

import org.example.dto.AuthorDto;
import org.example.dto.ExternalAuthorDto;
import org.example.mapper.DtoMapper;
import org.example.model.Author;
import org.example.service.AuthorService;
import org.example.service.ExternalAuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final ExternalAuthorService externalAuthorService;
    private final DtoMapper mapper;

    public AuthorController(AuthorService authorService, ExternalAuthorService externalAuthorService, DtoMapper mapper) {
        this.authorService = authorService;
        this.externalAuthorService = externalAuthorService;
        this.mapper = mapper;
    }

    /**
     * GET /api/authors - получить всех авторов
     */
    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        List<Author> authors = authorService.listAllAuthors();
        return ResponseEntity.ok(mapper.toAuthorDtoList(authors));
    }

    /**
     * POST /api/authors - создать нового автора
     */
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto authorDto) {
        Author author = authorService.createAuthor(authorDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAuthorDto(author));
    }

    /**
     * GET /api/authors/external/south-america - получить авторов из внешнего микросервиса (South America Authors Service)
     * Параметр name опциональный для поиска по имени
     * Примеры:
     * - GET /api/authors/external/south-america - все авторы
     * - GET /api/authors/external/south-america?name=Paulo - авторы с именем Paulo
     */
    @GetMapping("/external/south-america")
    public ResponseEntity<List<ExternalAuthorDto>> getSouthAmericaAuthors(
            @RequestParam(value = "name", required = false) String name) {
        List<ExternalAuthorDto> authors = externalAuthorService.getSouthAmericaAuthors(name);
        return ResponseEntity.ok(authors);
    }
}
