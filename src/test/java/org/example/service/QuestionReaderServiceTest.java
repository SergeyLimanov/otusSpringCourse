package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.junit.Assert.*;

public class QuestionReaderServiceTest {

    private QuestionReaderService readerService;

    @Before
    public void setUp() {
        readerService = new QuestionReaderService(new ClassPathResource("questions.csv"));
    }

    @Test
    public void testReadQuestions() {
        List<Question> questions = readerService.readQuestions();

        assertNotNull(questions);
        assertEquals(5, questions.size());

        Question q0 = questions.get(0);
        assertEquals(QuestionType.MULTIPLE_CHOICE, q0.getType());
        assertEquals("What is 2 + 2?", q0.getText());
        assertEquals(3, q0.getOptions().size());
        assertEquals("4", q0.getOptions().get(0));

        Question q2 = questions.get(2);
        assertEquals(QuestionType.FREE_TEXT, q2.getType());
        assertTrue(q2.getOptions().isEmpty());
    }
}