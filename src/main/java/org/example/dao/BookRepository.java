package org.example.dao;

import org.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"author", "genre", "comments"})
    Optional<Book> findWithDetailsById(Long id);

    // Для борьбы с N+1 при listBooks
    @EntityGraph(attributePaths = {"author", "genre"})
    @Override
    List<Book> findAll();

    List<Book> findAllByAuthorId(Long authorId);
    long countByGenreId(Long genreId);


}
