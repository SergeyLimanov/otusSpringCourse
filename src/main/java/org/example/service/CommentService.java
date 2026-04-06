package org.example.service;

import org.example.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByBookId(Long bookId);
    Comment addComment(Long bookId, String text);
    void deleteComment(Long commentId);
    Comment updateComment(Long commentId, String text);
}
