package org.example.model;

import java.util.List;

public class Question {
    private final QuestionType type;
    private final String text;
    private final List<String> options;
    private final String correctAnswer;

    public Question(QuestionType type, String text, List<String> options, String correctAnswer) {
        this.type = type;
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public QuestionType getType() { return type; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
}