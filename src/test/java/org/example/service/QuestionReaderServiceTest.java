package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionReaderServiceTest {

    @Test
    void shouldReadQuestionsFromCsv() {

        var resource = new ClassPathResource("questions-test.csv");
        var service = new QuestionReaderService(resource);


        List<Question> questions = service.readQuestions();

        assertEquals(2, questions.size());

        Question q1 = questions.get(0);
        assertEquals(QuestionType.MULTIPLE_CHOICE, q1.getType());
        assertEquals("What is 2+2?", q1.getText());
        assertEquals(List.of("3", "4", "5"), q1.getOptions());
        assertEquals("4", q1.getCorrectAnswer());

        Question q2 = questions.get(1);
        assertEquals(QuestionType.FREE_TEXT, q2.getType());
        assertEquals("What is Java?", q2.getText());
        assertTrue(q2.getOptions().isEmpty());
        assertEquals("A programming language", q2.getCorrectAnswer());
    }
}