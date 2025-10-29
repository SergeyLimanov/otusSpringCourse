package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

@Service
public class QuestionReaderService implements QuestionReader {

    private final Resource questionsResource;

    @Autowired
    public QuestionReaderService(@Value("${app.questions.file}") String fileName) {
        this.questionsResource = new ClassPathResource(fileName);
    }
    public QuestionReaderService(Resource questionsResource) {
        this.questionsResource = questionsResource;
    }

    @Override
    public List<Question> readQuestions() {
        List<Question> questions = new ArrayList<>();
        try (Reader reader = new InputStreamReader(
                questionsResource.getInputStream(),
                StandardCharsets.UTF_8)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            for (CSVRecord record : records) {
                String typeStr = record.get("type");
                String questionText = record.get("question");
                String optionsStr = record.get("options");
                String correctAnswer = record.get("correctAnswer");

                QuestionType type = QuestionType.valueOf(typeStr);
                List<String> options = new ArrayList<>();
                if (!optionsStr.trim().isEmpty()) {
                    for (String opt : optionsStr.split(",")) {
                        options.add(opt.trim());
                    }
                }

                questions.add(new Question(type, questionText, options, correctAnswer));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read questions from CSV", e);
        }
        return questions;
    }
}