package org.example.dao;

import org.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByBookId(Long bookId);
    List<Comment> findAllByBookId(Long bookId);

    @Query("SELECT c FROM Comment c WHERE c.text LIKE %:keyword%")
    List<Comment> findByTextContaining(@Param("keyword") String keyword);
}