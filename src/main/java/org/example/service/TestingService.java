package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestingService {

    private final QuestionReaderService readerService;
    private final IOService ioService;
    private final int passingScore;

    public TestingService(QuestionReaderService readerService,
                          IOService ioService,
                          @Value("${app.passing.score}") int passingScore) {
        this.readerService = readerService;
        this.ioService = ioService;
        this.passingScore = passingScore;
    }

    public int runTest() { // возвращаем результат для тестирования!

        List<Question> allQuestions = readerService.readQuestions();
        if (allQuestions.size() < 5) {
            throw new IllegalStateException("Not enough questions in CSV (need at least 5)");
        }

        int correctCount = 0;
        for (int i = 0; i < allQuestions.size(); i++) {
            Question q = allQuestions.get(i);
            ioService.print(String.format("%nQuestion %d: %s%n", i + 1, q.getText()));

            if (q.getType() == QuestionType.MULTIPLE_CHOICE) {
                for (int j = 0; j < q.getOptions().size(); j++) {
                    ioService.print(String.format("  %c) %s%n", 'A' + j, q.getOptions().get(j)));
                }
                ioService.print("Your answer (A, B, C...): ");
                String input = ioService.readLine().trim().toUpperCase();

                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    char choice = input.charAt(0);
                    int index = choice - 'A';
                    if (index >= 0 && index < q.getOptions().size()) {
                        String selectedOption = q.getOptions().get(index);
                        if (selectedOption.equalsIgnoreCase(q.getCorrectAnswer())) {
                            correctCount++;
                        }
                    }
                }
            } else {
                ioService.print("Your answer: ");
                String answer = ioService.readLine().trim();
                if (answer.equalsIgnoreCase(q.getCorrectAnswer())) {
                    correctCount++;
                }
            }
        }

        // Вывод результата);
        ioService.print(String.format("Correct answers: %d / 5%n", correctCount));
        if (correctCount >= passingScore) {
            ioService.print("Result: PASSED ✅\n");
        } else {
            ioService.print("Result: FAILED ❌\n");
        }

        return correctCount; // ← для тестов
    }
}
