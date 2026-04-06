package org.example.mapper;

import org.example.dto.*;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public AuthorDto toAuthorDto(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorDto(author.getId(), author.getName());
    }

    public GenreDto toGenreDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDto(genre.getId(), genre.getName());
    }

    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook() != null ? comment.getBook().getId() : null
        );
    }

    public BookDto toBookDto(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDto(
                book.getId(),
                book.getTitle(),
                toAuthorDto(book.getAuthor()),
                toGenreDto(book.getGenre()),
                book.getComments() != null
                        ? book.getComments().stream().map(this::toCommentDto).collect(Collectors.toList())
                        : Collections.emptyList()
        );
    }

    public BookDto toBookDtoWithoutComments(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDto(
                book.getId(),
                book.getTitle(),
                toAuthorDto(book.getAuthor()),
                toGenreDto(book.getGenre()),
                Collections.emptyList()
        );
    }

    public List<BookDto> toBookDtoList(List<Book> books) {
        if (books == null) {
            return Collections.emptyList();
        }
        return books.stream()
                .map(this::toBookDtoWithoutComments)
                .collect(Collectors.toList());
    }

    public List<AuthorDto> toAuthorDtoList(List<Author> authors) {
        if (authors == null) {
            return Collections.emptyList();
        }
        return authors.stream()
                .map(this::toAuthorDto)
                .collect(Collectors.toList());
    }

    public List<GenreDto> toGenreDtoList(List<Genre> genres) {
        if (genres == null) {
            return Collections.emptyList();
        }
        return genres.stream()
                .map(this::toGenreDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }
}
