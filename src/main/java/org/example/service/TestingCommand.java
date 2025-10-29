package org.example.service;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class TestingCommand {

    private final ITesting testingService;

    public TestingCommand(ITesting testingService) {
        this.testingService = testingService;
    }

    @ShellMethod(key = "start-test", value = "Start the student testing")
    public String startTest() {
        int score = testingService.runTest();
        return "Test completed. Score: " + score;
    }
}