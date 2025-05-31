package org.lucian.todos;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MainTest {

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // Initialize new output streams
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        
        // Redirect stdout and stderr for testing output
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    
    @AfterEach
    void restoreStreams() {
        // Restore original stdout and stderr
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Test basic assertion - setup verification")
    void testBasicAssertion() {
        // This is a trivial test to confirm JUnit 5 setup works
        assertTrue(true, "Basic assertion should pass");
        assertEquals(2, 1 + 1, "Basic math should work");
    }
    
    @Test
    @DisplayName("Test application name method")
    void testGetApplicationName() {
        // Test our simple utility method
        String appName = Main.getApplicationName();
        assertNotNull(appName, "Application name should not be null");
        assertEquals("Task Management System", appName, "Application name should match expected value");
    }
    
}
