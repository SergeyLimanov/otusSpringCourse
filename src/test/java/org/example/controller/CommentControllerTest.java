package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CommentDto;
import org.example.mapper.DtoMapper;
import org.example.model.Comment;
import org.example.security.JwtAuthenticationFilter;
import org.example.security.JwtTokenProvider;
import org.example.service.CommentService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private DtoMapper mapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser
    void shouldGetCommentsByBookId() throws Exception {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great book!");

        CommentDto commentDto = new CommentDto(1L, "Great book!", 1L);

        when(commentService.findCommentsByBookId(1L)).thenReturn(List.of(comment));
        when(mapper.toCommentDtoList(any())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/comments")
                        .param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Great book!"))
                .andExpect(jsonPath("$[0].bookId").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListForBookWithoutComments() throws Exception {
        when(commentService.findCommentsByBookId(999L)).thenReturn(new ArrayList<>());
        when(mapper.toCommentDtoList(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/comments")
                        .param("bookId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void shouldCreateNewComment() throws Exception {
        CommentDto requestDto = new CommentDto(null, "Very interesting!", 1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Very interesting!");

        CommentDto responseDto = new CommentDto(1L, "Very interesting!", 1L);

        when(commentService.addComment(1L, "Very interesting!")).thenReturn(comment);
        when(mapper.toCommentDto(comment)).thenReturn(responseDto);

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Very interesting!"))
                .andExpect(jsonPath("$.bookId").value(1));
    }


    @Test
    @WithMockUser
    void shouldUpdateComment() throws Exception {
        CommentDto requestDto = new CommentDto(null, "Updated comment", null);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Updated comment");

        CommentDto responseDto = new CommentDto(1L, "Updated comment", 1L);

        when(commentService.updateComment(1L, "Updated comment")).thenReturn(comment);
        when(mapper.toCommentDto(comment)).thenReturn(responseDto);

        mockMvc.perform(put("/api/comments/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Updated comment"))
                .andExpect(jsonPath("$.bookId").value(1));
    }



    @Test
    @WithMockUser
    void shouldDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/{id}", 1L).with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1L);
    }



    @Test
    @WithMockUser
    void shouldCreateMultipleCommentsForSameBook() throws Exception {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("First comment");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Second comment");

        CommentDto dto1 = new CommentDto(1L, "First comment", 1L);
        CommentDto dto2 = new CommentDto(2L, "Second comment", 1L);

        when(commentService.addComment(1L, "First comment")).thenReturn(comment1);
        when(commentService.addComment(1L, "Second comment")).thenReturn(comment2);
        when(mapper.toCommentDto(comment1)).thenReturn(dto1);
        when(mapper.toCommentDto(comment2)).thenReturn(dto2);

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentDto(null, "First comment", 1L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("First comment"));

        mockMvc.perform(post("/api/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentDto(null, "Second comment", 1L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Second comment"));
    }
}
