package org.example.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.service.BookService;
import org.example.service.AuthorService;

/**
 * Custom Health Indicator for Application data availability check
 */
@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    private final BookService bookService;
    private final AuthorService authorService;

    @Autowired
    public ApplicationHealthIndicator(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @Override
    public Health health() {
        try {
            long bookCount = bookService.findAll().size();
            long authorCount = authorService.findAll().size();

            if (bookCount > 0 && authorCount > 0) {
                return Health.up()
                        .withDetail("application", "Library Application")
                        .withDetail("status", "Data available")
                        .withDetail("books_count", bookCount)
                        .withDetail("authors_count", authorCount)
                        .build();
            } else {
                return Health.degraded()
                        .withDetail("application", "Library Application")
                        .withDetail("status", "Limited data available")
                        .withDetail("books_count", bookCount)
                        .withDetail("authors_count", authorCount)
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("application", "Library Application")
                    .withDetail("status", "Data access failed")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
