package org.example;

import org.example.service.QuestionDisplayService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context =
                     new ClassPathXmlApplicationContext("applicationContext.xml")) {

            QuestionDisplayService displayService = context.getBean(QuestionDisplayService.class);
            displayService.displayAllQuestions();
        }
    }
}