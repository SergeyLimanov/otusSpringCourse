package org.example.controller;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequest;
import org.example.dto.UpdateBookRequest;
import org.example.mapper.DtoMapper;
import org.example.model.Book;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final DtoMapper mapper;

    public BookController(BookService bookService, DtoMapper mapper) {
        this.bookService = bookService;
        this.mapper = mapper;
    }

    /**
     * GET /api/books - получить все книги
     */
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(mapper.toBookDtoList(books));
    }

    /**
     * GET /api/books/{id} - получить книгу по ID с комментариями
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toBookDto(book));
    }

    /**
     * POST /api/books - создать новую книгу
     */
    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody CreateBookRequest request) {
        Book book = bookService.createBook(
                request.getTitle(),
                request.getAuthorName(),
                request.getGenreName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toBookDto(book));
    }

    /**
     * PUT /api/books/{id} - обновить книгу
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(
            @PathVariable Long id,
            @RequestBody UpdateBookRequest request) {
        Book updated = bookService.updateBook(
                id,
                request.getTitle(),
                request.getAuthorName(),
                request.getGenreName()
        );
        return ResponseEntity.ok(mapper.toBookDto(updated));
    }

    /**
     * DELETE /api/books/{id} - удалить книгу
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
