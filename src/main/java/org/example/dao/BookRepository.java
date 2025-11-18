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

    @Query("SELECT b FROM Book b WHERE b.author.name = :authorName")
    List<Book> findAllByAuthorName(@Param("authorName") String authorName);

    @Query(value = "SELECT COUNT(*) FROM books b JOIN genres g ON b.genre_id = g.id WHERE g.name = :genreName", nativeQuery = true)
    long countBooksByGenreName(@Param("genreName") String genreName);
}
