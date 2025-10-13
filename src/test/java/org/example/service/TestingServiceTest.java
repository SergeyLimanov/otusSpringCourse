package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestingServiceTest {

    @Mock
    private QuestionReaderService readerService;

    @Mock
    private IOService ioService;

    @Test
    void shouldReturnCorrectScoreForMixedAnswers() {
        // Arrange: 5 вопросов
        var questions = Arrays.asList(
                new Question(QuestionType.MULTIPLE_CHOICE, "What is 2+2?", List.of("3", "4", "5"), "4"),
                new Question(QuestionType.FREE_TEXT, "What is Java?", List.of(), "A programming language"),
                new Question(QuestionType.MULTIPLE_CHOICE, "Boolean type?", List.of("int", "boolean"), "boolean"),
                new Question(QuestionType.FREE_TEXT, "OOP principle?", List.of(), "Encapsulation"),
                new Question(QuestionType.MULTIPLE_CHOICE, "Java keyword?", List.of("final", "include"), "final")
        );
        when(readerService.readQuestions()).thenReturn(questions);


        when(ioService.readLine())
                .thenReturn("B", "A programming language", "B", "Encapsulation", "A");

        TestingService testingService = new TestingService(readerService, ioService, 3);

        // Act
        int score = testingService.runTest();

        assertEquals(5, score);
    }

    @Test
    void shouldHandleWrongAnswers() {
        var questions = Arrays.asList(
                new Question(QuestionType.MULTIPLE_CHOICE, "Test1", List.of("A", "B"), "B"),
                new Question(QuestionType.FREE_TEXT, "Test2", List.of(), "Java"),
                new Question(QuestionType.MULTIPLE_CHOICE, "Test3", List.of("X", "Y"), "X"),
                new Question(QuestionType.FREE_TEXT, "Test4", List.of(), "OOP"),
                new Question(QuestionType.MULTIPLE_CHOICE, "Test5", List.of("P", "Q"), "Q")
        );
        when(readerService.readQuestions()).thenReturn(questions);


        when(ioService.readLine())
                .thenReturn("B", "WRONG", "A", "OOP", "B");

        TestingService testingService = new TestingService(readerService, ioService, 3);
        int score = testingService.runTest();

        assertEquals(4, score); // Q1, Q3, Q4, Q5 — правильные
    }

    @Test
    void shouldCorrectlyScoreFreeTextAnswer() {
        var questions = Arrays.asList(
                new Question(QuestionType.MULTIPLE_CHOICE, "Test1", List.of("A", "B"), "B"),
                new Question(QuestionType.FREE_TEXT, "Test2", List.of(), "Encapsulation"),
                new Question(QuestionType.MULTIPLE_CHOICE, "Test3", List.of("X", "Y"), "X"),
                new Question(QuestionType.FREE_TEXT, "Test4", List.of(), "Platform"),
                new Question(QuestionType.MULTIPLE_CHOICE, "Test5", List.of("P", "Q"), "Q")
        );
        when(readerService.readQuestions()).thenReturn(questions);

        when(ioService.readLine())
                .thenReturn("B", "Encapsulation", "A", "Platform", "B");

        TestingService service = new TestingService(readerService, ioService, 3);
        assertEquals(5, service.runTest());
    }
}