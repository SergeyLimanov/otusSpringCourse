package org.example.dao;

import org.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByBookId(Long bookId);
    List<Comment> findAllByBookId(Long bookId);
}