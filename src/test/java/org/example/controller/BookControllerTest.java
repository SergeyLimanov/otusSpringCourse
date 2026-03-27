package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.BookDto;
import org.example.dto.AuthorDto;
import org.example.dto.GenreDto;
import org.example.dto.CreateBookRequest;
import org.example.dto.UpdateBookRequest;
import org.example.mapper.DtoMapper;
import org.example.model.Book;
import org.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private DtoMapper mapper;

    @Test
    @WithMockUser
    void shouldGetAllBooks() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        BookDto bookDto = new BookDto(1L, "Test Book",
                new AuthorDto(1L, "Test Author"),
                new GenreDto(1L, "Test Genre"),
                new ArrayList<>());

        when(bookService.findAll()).thenReturn(List.of(book));
        when(mapper.toBookDtoList(anyList())).thenReturn(List.of(bookDto));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author.name").value("Test Author"))
                .andExpect(jsonPath("$[0].genre.name").value("Test Genre"));
    }

    @Test
    @WithMockUser
    void shouldGetBookById() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        BookDto bookDto = new BookDto(1L, "Test Book",
                new AuthorDto(1L, "Test Author"),
                new GenreDto(1L, "Test Genre"),
                new ArrayList<>());

        when(bookService.findById(1L)).thenReturn(book);
        when(mapper.toBookDto(book)).thenReturn(bookDto);

        mockMvc.perform(get("/api/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author.name").value("Test Author"))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"))
                .andExpect(jsonPath("$.comments").isArray());
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForNonExistentBook() throws Exception {
        when(bookService.findById(99999L)).thenReturn(null);

        mockMvc.perform(get("/api/books/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldCreateNewBook() throws Exception {
        CreateBookRequest request = new CreateBookRequest("New Book", "Test Author", "Test Genre");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("New Book");

        BookDto bookDto = new BookDto(1L, "New Book",
                new AuthorDto(1L, "Test Author"),
                new GenreDto(1L, "Test Genre"),
                new ArrayList<>());

        when(bookService.createBook("New Book", "Test Author", "Test Genre")).thenReturn(book);
        when(mapper.toBookDto(book)).thenReturn(bookDto);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author.name").value("Test Author"))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"));
    }

    @Test
    @WithMockUser
    void shouldCreateBookWithNewAuthorAndGenre() throws Exception {
        CreateBookRequest request = new CreateBookRequest("Book", "New Author", "New Genre");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Book");

        BookDto bookDto = new BookDto(2L, "Book",
                new AuthorDto(2L, "New Author"),
                new GenreDto(2L, "New Genre"),
                new ArrayList<>());

        when(bookService.createBook("Book", "New Author", "New Genre")).thenReturn(book);
        when(mapper.toBookDto(book)).thenReturn(bookDto);

        mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Book"))
                .andExpect(jsonPath("$.author.name").value("New Author"))
                .andExpect(jsonPath("$.genre.name").value("New Genre"));
    }

    @Test
    @WithMockUser
    void shouldUpdateBook() throws Exception {
        UpdateBookRequest request = new UpdateBookRequest("Updated Title", "Test Author", "Test Genre");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Updated Title");

        BookDto bookDto = new BookDto(1L, "Updated Title",
                new AuthorDto(1L, "Test Author"),
                new GenreDto(1L, "Test Genre"),
                new ArrayList<>());

        when(bookService.updateBook(1L, "Updated Title", "Test Author", "Test Genre")).thenReturn(book);
        when(mapper.toBookDto(book)).thenReturn(bookDto);

        mockMvc.perform(put("/api/books/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author.name").value("Test Author"))
                .andExpect(jsonPath("$.genre.name").value("Test Genre"));
    }

    @Test
    @WithMockUser
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(1L);

        mockMvc.perform(delete("/api/books/{id}", 1L).with(csrf()))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser
    void shouldDeleteNonExistentBookWithoutError() throws Exception {
        doNothing().when(bookService).deleteById(99999L);

        mockMvc.perform(delete("/api/books/{id}", 99999L).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
