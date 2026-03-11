package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.GenreDto;
import org.example.mapper.DtoMapper;
import org.example.model.Genre;
import org.example.service.GenreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenreService genreService;

    @MockBean
    private DtoMapper mapper;

    @Test
    void shouldGetAllGenres() throws Exception {
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Test Genre");

        GenreDto genreDto = new GenreDto(1L, "Test Genre");

        when(genreService.listAllGenres()).thenReturn(List.of(genre));
        when(mapper.toGenreDtoList(any())).thenReturn(List.of(genreDto));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Genre"));
    }

    @Test
    void shouldCreateNewGenre() throws Exception {
        GenreDto requestDto = new GenreDto(null, "New Genre");

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("New Genre");

        GenreDto responseDto = new GenreDto(1L, "New Genre");

        when(genreService.createGenre("New Genre")).thenReturn(genre);
        when(mapper.toGenreDto(genre)).thenReturn(responseDto);

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Genre"));
    }

    @Test
    void shouldReturnAllGenresIncludingNewlyCreated() throws Exception {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Genre 1");

        Genre genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Genre 2");

        GenreDto dto1 = new GenreDto(1L, "Genre 1");
        GenreDto dto2 = new GenreDto(2L, "Genre 2");

        when(genreService.listAllGenres()).thenReturn(List.of(genre1, genre2));
        when(mapper.toGenreDtoList(any())).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("Genre 1", "Genre 2")));
    }

    @Test
    void shouldCreateGenreWithEmptyIdInRequest() throws Exception {
        GenreDto requestDto = new GenreDto(999L, "Test Name");

        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Test Name");

        GenreDto responseDto = new GenreDto(1L, "Test Name");

        when(genreService.createGenre("Test Name")).thenReturn(genre);
        when(mapper.toGenreDto(genre)).thenReturn(responseDto);

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id").value(not(999)))
                .andExpect(jsonPath("$.name").value("Test Name"));
    }
}
