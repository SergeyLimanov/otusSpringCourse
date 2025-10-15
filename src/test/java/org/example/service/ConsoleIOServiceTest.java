package org.example.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleIOServiceTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreSystemStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void shouldPrintMessageToConsole() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));

        ConsoleIOService ioService = new ConsoleIOService();
        String message = "Hello, student!";
        ioService.print(message);

        assertEquals(message, outContent.toString(StandardCharsets.UTF_8));
    }

    @Test
    void shouldReadLineFromInput() {
        String input = "Alice\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleIOService ioService = new ConsoleIOService();
        String result = ioService.readLine();

        assertEquals("Alice", result);
    }

    @Test
    void shouldReadEmptyLine() {
        System.setIn(new ByteArrayInputStream("\n".getBytes(StandardCharsets.UTF_8)));

        ConsoleIOService ioService = new ConsoleIOService();
        String result = ioService.readLine();

        assertEquals("", result);
    }

    @Test
    void shouldHandleMultipleReads() {
        String input = "Math\nPhysics\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleIOService ioService = new ConsoleIOService();

        String first = ioService.readLine();
        String second = ioService.readLine();

        assertEquals("Math", first);
        assertEquals("Physics", second);
    }

    @Test
    void shouldReadLineWithoutTrailingNewline_EOF() {
        String input = "Final answer";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        ConsoleIOService ioService = new ConsoleIOService();
        String result = ioService.readLine();

        assertEquals("Final answer", result);
    }
}