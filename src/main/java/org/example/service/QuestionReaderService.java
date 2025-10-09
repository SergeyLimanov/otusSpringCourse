package org.example.service;

import org.example.model.Question;
import org.example.model.QuestionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuestionReaderService {

    private final Resource questionsResource;

    public QuestionReaderService(Resource questionsResource) {
        this.questionsResource = questionsResource;
    }

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

                QuestionType type = QuestionType.valueOf(typeStr);
                List<String> options = new ArrayList<>();
                if (!optionsStr.isEmpty()) {
                    for (String opt : optionsStr.split(",")) {
                        options.add(opt.trim());
                    }
                }

                questions.add(new Question(type, questionText, options));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read questions from CSV", e);
        }
        return questions;
    }
}
