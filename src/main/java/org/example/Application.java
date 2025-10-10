package org.example;


import org.example.config.AppConfig;
import org.example.service.TestingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            TestingService testingService = context.getBean(TestingService.class);
            testingService.runTest();
        }
    }
}