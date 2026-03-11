package org.example.service;

import org.example.dao.CommentRepository;
import org.example.model.Book;
import org.example.model.Comment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookService bookService;



    public CommentServiceImpl(CommentRepository commentRepository, BookService bookService) {
        this.commentRepository = commentRepository;
        this.bookService = bookService;
    }

    @Override
    public List<Comment> findCommentsByBookId(Long bookId) {
        return commentRepository.findAllByBookId(bookId);
    }

    @Transactional
    @Override
    public Comment addComment(Long bookId, String text) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            throw new NoSuchElementException("Book not found with id: " + bookId);
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setBook(book);

        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NoSuchElementException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public Comment updateComment(Long commentId, String text) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));
        comment.setText(text);
        return commentRepository.save(comment);
    }
}
