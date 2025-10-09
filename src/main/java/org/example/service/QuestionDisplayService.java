package org.example.service;

import org.example.model.Question;

import java.io.PrintStream;
import java.util.List;

public class QuestionDisplayService {

    private final QuestionReaderService readerService;
    private final PrintStream output;

    public QuestionDisplayService(QuestionReaderService readerService, PrintStream output) {
        this.readerService = readerService;
        this.output = output;
    }

    public void displayAllQuestions() {
        List<Question> questions = readerService.readQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            output.printf("Question %d: %s%n", i + 1, q.getText());

            if (!q.getOptions().isEmpty()) {
                for (int j = 0; j < q.getOptions().size(); j++) {
                    output.printf("  %c) %s%n", 'A' + j, q.getOptions().get(j));
                }
            } else {
                output.println("  [Free text answer]");
            }
            output.println();
        }
    }
}