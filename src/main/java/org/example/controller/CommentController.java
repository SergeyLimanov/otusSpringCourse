package org.example.controller;

import org.example.dto.CommentDto;
import org.example.mapper.DtoMapper;
import org.example.model.Comment;
import org.example.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final DtoMapper mapper;

    public CommentController(CommentService commentService, DtoMapper mapper) {
        this.commentService = commentService;
        this.mapper = mapper;
    }

    /**
     * GET /api/comments?bookId={bookId} - получить комментарии к книге
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByBookId(@RequestParam Long bookId) {
        List<Comment> comments = commentService.findCommentsByBookId(bookId);
        return ResponseEntity.ok(mapper.toCommentDtoList(comments));
    }

    /**
     * POST /api/comments - создать новый комментарий
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        Comment comment = commentService.addComment(commentDto.getBookId(), commentDto.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCommentDto(comment));
    }

    /**
     * PUT /api/comments/{id} - обновить комментарий
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody CommentDto commentDto) {
        Comment updated = commentService.updateComment(id, commentDto.getText());
        return ResponseEntity.ok(mapper.toCommentDto(updated));
    }

    /**
     * DELETE /api/comments/{id} - удалить комментарий
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
