package org.example.dao;

import org.example.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByName(String name);
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > :minBooks")
    List<Author> findAuthorsWithMoreThanNBooks(@Param("minBooks") int minBooks);
}