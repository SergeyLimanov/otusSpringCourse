package org.example;


import org.example.config.AppConfig;
import org.example.service.ITesting;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ITesting testingService = context.getBean(ITesting.class);
            testingService.runTest();
        }
    }
}