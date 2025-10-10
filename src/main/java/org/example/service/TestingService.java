package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
public class TestingService {

    private final QuestionReaderService readerService;
    private final int passingScore;

    public TestingService(QuestionReaderService readerService,
                          @Value("${app.passing.score}") int passingScore) {
        this.readerService = readerService;
        this.passingScore = passingScore;
    }

    public void runTest() {
        Scanner scanner = new Scanner(System.in);

        List<Question> allQuestions = readerService.readQuestions();
        if (allQuestions.size() < 5) {
            throw new IllegalStateException("Not enough questions in CSV (need at least 5)");
        }

        List<Question> questions = allQuestions;

        int correctCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.printf("%nQuestion %d: %s%n", i + 1, q.getText());

            if (q.getType() == QuestionType.MULTIPLE_CHOICE) {
                for (int j = 0; j < q.getOptions().size(); j++) {
                    System.out.printf("  %c) %s%n", 'A' + j, q.getOptions().get(j));
                }
                System.out.print("Your answer (A, B, C...): ");
                String input = scanner.nextLine().trim().toUpperCase();

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
                System.out.print("Your answer: ");
                String answer = scanner.nextLine().trim();
                if (answer.equalsIgnoreCase(q.getCorrectAnswer())) {
                    correctCount++;
                }
            }
        }

        // Вывод результата
        System.out.printf("Correct answers: %d / 5%n", correctCount);
        if (correctCount >= passingScore) {
            System.out.println("Result: PASSED ✅");
        } else {
            System.out.println("Result: FAILED ❌");
        }
    }
}
