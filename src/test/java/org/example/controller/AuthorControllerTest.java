package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AuthorDto;
import org.example.mapper.DtoMapper;
import org.example.model.Author;
import org.example.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private DtoMapper mapper;

    @Test
    @WithMockUser
    void shouldGetAllAuthors() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        AuthorDto authorDto = new AuthorDto(1L, "Test Author");

        when(authorService.listAllAuthors()).thenReturn(List.of(author));
        when(mapper.toAuthorDtoList(any())).thenReturn(List.of(authorDto));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Author"));
    }

    @Test
    @WithMockUser
    void shouldCreateNewAuthor() throws Exception {
        AuthorDto requestDto = new AuthorDto(null, "New Author");

        Author author = new Author();
        author.setId(1L);
        author.setName("New Author");

        AuthorDto responseDto = new AuthorDto(1L, "New Author");

        when(authorService.createAuthor("New Author")).thenReturn(author);
        when(mapper.toAuthorDto(author)).thenReturn(responseDto);

        mockMvc.perform(post("/api/authors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Author"));
    }

    @Test
    @WithMockUser
    void shouldReturnAllAuthorsIncludingNewlyCreated() throws Exception {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("Author 1");

        Author author2 = new Author();
        author2.setId(2L);
        author2.setName("Author 2");

        AuthorDto dto1 = new AuthorDto(1L, "Author 1");
        AuthorDto dto2 = new AuthorDto(2L, "Author 2");

        when(authorService.listAllAuthors()).thenReturn(List.of(author1, author2));
        when(mapper.toAuthorDtoList(any())).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("Author 1", "Author 2")));
    }

    @Test
    @WithMockUser
    void shouldCreateAuthorWithEmptyIdInRequest() throws Exception {
        AuthorDto requestDto = new AuthorDto(999L, "Test Name");

        Author author = new Author();
        author.setId(1L);
        author.setName("Test Name");

        AuthorDto responseDto = new AuthorDto(1L, "Test Name");

        when(authorService.createAuthor("Test Name")).thenReturn(author);
        when(mapper.toAuthorDto(author)).thenReturn(responseDto);

        mockMvc.perform(post("/api/authors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id").value(not(999)))
                .andExpect(jsonPath("$.name").value("Test Name"));
    }
}
