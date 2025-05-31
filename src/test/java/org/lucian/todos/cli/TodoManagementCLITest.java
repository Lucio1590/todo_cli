package org.lucian.todos.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lucian.todos.cli.handler.AuthenticationCommandHandler;
import org.lucian.todos.cli.menu.MainMenu;
import org.lucian.todos.cli.util.CLIUtils;
import org.lucian.todos.dao.DAOFactory;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.service.AuthenticationService;
import org.lucian.todos.service.ProjectService;
import org.lucian.todos.service.TodoService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the TodoManagementCLI class using Mockito for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
class TodoManagementCLITest {

    @Mock
    private Scanner mockScanner;
    
    @Mock
    private TodoService mockTodoService;
    
    @Mock
    private ProjectService mockProjectService;
    
    @Mock
    private AuthenticationService mockAuthService;
    
    @Mock
    private AuthenticationCommandHandler mockAuthHandler;
    
    @Mock
    private MainMenu mockMainMenu;
    
    @Mock
    private DAOFactory mockDAOFactory;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreSystemStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    @Test
    @DisplayName("Test display welcome with successful statistics")
    void testDisplayWelcomeWithSuccessfulStatistics() throws Exception {
        // Setup
        try (MockedStatic<DAOFactory> daoFactoryMock = mockStatic(DAOFactory.class);
             MockedStatic<CLIUtils> cliUtilsMock = mockStatic(CLIUtils.class)) {
            
            // Mock static methods
            daoFactoryMock.when(DAOFactory::getInstance).thenReturn(mockDAOFactory);
            cliUtilsMock.when(() -> CLIUtils.clearScreen()).then(invocation -> { return null; });
            cliUtilsMock.when(() -> CLIUtils.printHeader(anyString())).then(invocation -> { return null; });
            cliUtilsMock.when(() -> CLIUtils.printSuccess(anyString())).then(invocation -> { return null; });
            cliUtilsMock.when(() -> CLIUtils.waitForKeyPress(any(Scanner.class))).then(invocation -> { return null; });
            
            // Mock DAO and service behavior
            when(mockDAOFactory.getUserDAO()).thenReturn(null); // Mock as needed
            when(mockDAOFactory.getTodoDAO()).thenReturn(null); // Mock as needed
            when(mockDAOFactory.getProjectDAO()).thenReturn(null); // Mock as needed
            
            // Mock statistics
            TodoService.TodoStatistics mockTodoStats = mock(TodoService.TodoStatistics.class);
            ProjectService.ProjectStatistics mockProjectStats = mock(ProjectService.ProjectStatistics.class);
            
            when(mockTodoService.getTodoStatistics()).thenReturn(mockTodoStats);
            when(mockProjectService.getProjectStatistics()).thenReturn(mockProjectStats);
            
            when(mockTodoStats.getTotalTodos()).thenReturn(10L);
            when(mockTodoStats.getTodoTodos()).thenReturn(5L);
            when(mockTodoStats.getInProgressTodos()).thenReturn(2L);
            when(mockTodoStats.getCompletedTodos()).thenReturn(3L);
            when(mockTodoStats.getOverdueTodos()).thenReturn(0L);
            
            when(mockProjectStats.getTotalProjects()).thenReturn(5L);
            when(mockProjectStats.getActiveProjects()).thenReturn(3L);
            when(mockProjectStats.getCompletedProjects()).thenReturn(2L);
            
            // Create CLI with mocked dependencies
            TodoManagementCLI cli = createCLI();
            
            // Call the private method using reflection
            java.lang.reflect.Method displayWelcomeMethod = TodoManagementCLI.class.getDeclaredMethod("displayWelcome");
            displayWelcomeMethod.setAccessible(true);
            
            // Execute
            assertDoesNotThrow(() -> displayWelcomeMethod.invoke(cli));
            
            // Capture output
            String output = outContent.toString();
            
            // Verify expected output
            assertTrue(output.contains("Welcome to the Todo Management System"));
            assertTrue(output.contains("Current System Status"));
        }
    }
    
    @Test
    @DisplayName("Test display welcome with database exception")
    void testDisplayWelcomeWithDatabaseException() throws Exception {
        // Setup
        try (MockedStatic<DAOFactory> daoFactoryMock = mockStatic(DAOFactory.class);
             MockedStatic<CLIUtils> cliUtilsMock = mockStatic(CLIUtils.class)) {
            
            // Mock static methods
            daoFactoryMock.when(DAOFactory::getInstance).thenReturn(mockDAOFactory);
            cliUtilsMock.when(() -> CLIUtils.clearScreen()).then(invocation -> { return null; });
            cliUtilsMock.when(() -> CLIUtils.printHeader(anyString())).then(invocation -> { return null; });
            cliUtilsMock.when(() -> CLIUtils.printSuccess(anyString())).then(invocation -> { return null; });
            cliUtilsMock.when(() -> CLIUtils.waitForKeyPress(any(Scanner.class))).then(invocation -> { return null; });
            
            // Mock database exception
            when(mockTodoService.getTodoStatistics()).thenThrow(new DatabaseException("Database error"));
            
            // Create CLI with mocked dependencies
            TodoManagementCLI cli = createCLI();
            
            // Call the private method using reflection
            java.lang.reflect.Method displayWelcomeMethod = TodoManagementCLI.class.getDeclaredMethod("displayWelcome");
            displayWelcomeMethod.setAccessible(true);
            
            // Execute
            assertDoesNotThrow(() -> displayWelcomeMethod.invoke(cli));
            
            // Capture output
            String output = outContent.toString();
            
            // Verify expected output
            assertTrue(output.contains("System ready for use"));
        }
    }
    
    @Test
    @DisplayName("Test handleAuthenticationFlow with successful login")
    void testHandleAuthenticationFlowWithSuccessfulLogin() throws Exception {
        // Setup
        when(mockAuthService.isLoggedIn()).thenReturn(false);
        when(mockAuthHandler.handleLogin()).thenReturn(true);
        
        // Create CLI with mocked dependencies
        TodoManagementCLI cli = createCLI();
        
        // Call the private method using reflection
        java.lang.reflect.Method authFlowMethod = TodoManagementCLI.class.getDeclaredMethod("handleAuthenticationFlow");
        authFlowMethod.setAccessible(true);
        
        // Execute
        Object result = authFlowMethod.invoke(cli);
        
        // Verify
        assertTrue((Boolean) result);
        verify(mockAuthHandler, times(1)).handleLogin();
    }
    
    @Test
    @DisplayName("Test handleAuthenticationFlow when already logged in")
    void testHandleAuthenticationFlowWhenAlreadyLoggedIn() throws Exception {
        // Setup
        when(mockAuthService.isLoggedIn()).thenReturn(true);
        
        // Create CLI with mocked dependencies
        TodoManagementCLI cli = createCLI();
        
        // Call the private method using reflection
        java.lang.reflect.Method authFlowMethod = TodoManagementCLI.class.getDeclaredMethod("handleAuthenticationFlow");
        authFlowMethod.setAccessible(true);
        
        // Execute
        Object result = authFlowMethod.invoke(cli);
        
        // Verify
        assertTrue((Boolean) result);
        verify(mockAuthHandler, times(0)).handleLogin(); // Should not call login if already authenticated
    }
    
    @Test
    @DisplayName("Test handle main menu choice with todos option")
    void testHandleMainMenuChoiceWithTodosOption() throws Exception {
        // Setup
        String input = "1\n"; // Input "1" for todos menu
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        try (MockedStatic<CLIUtils> cliUtilsMock = mockStatic(CLIUtils.class)) {
            cliUtilsMock.when(() -> CLIUtils.getInput(any(Scanner.class), anyString())).thenReturn("1");
            
            // Create CLI with mocked dependencies
            TodoManagementCLI cli = createCLI();
            
            // Call the private method using reflection
            java.lang.reflect.Method handleMenuMethod = TodoManagementCLI.class.getDeclaredMethod("handleMainMenuChoice");
            handleMenuMethod.setAccessible(true);
            
            // Execute
            assertDoesNotThrow(() -> handleMenuMethod.invoke(cli));
            
            // Verify
            verify(mockMainMenu, times(1)).handleTodoMenu();
        }
    }
    
    // Helper method to create CLI instance with injected mocks
    private TodoManagementCLI createCLI() throws Exception {
        // Use reflection to create an instance and set mocked fields
        TodoManagementCLI cli = new TodoManagementCLI() {
            // Override constructor to avoid real dependencies
        };
        
        // Use reflection to set private fields
        java.lang.reflect.Field scannerField = TodoManagementCLI.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(cli, mockScanner);
        
        java.lang.reflect.Field todoServiceField = TodoManagementCLI.class.getDeclaredField("todoService");
        todoServiceField.setAccessible(true);
        todoServiceField.set(cli, mockTodoService);
        
        java.lang.reflect.Field projectServiceField = TodoManagementCLI.class.getDeclaredField("projectService");
        projectServiceField.setAccessible(true);
        projectServiceField.set(cli, mockProjectService);
        
        java.lang.reflect.Field authServiceField = TodoManagementCLI.class.getDeclaredField("authService");
        authServiceField.setAccessible(true);
        authServiceField.set(cli, mockAuthService);
        
        java.lang.reflect.Field authHandlerField = TodoManagementCLI.class.getDeclaredField("authHandler");
        authHandlerField.setAccessible(true);
        authHandlerField.set(cli, mockAuthHandler);
        
        java.lang.reflect.Field mainMenuField = TodoManagementCLI.class.getDeclaredField("mainMenu");
        mainMenuField.setAccessible(true);
        mainMenuField.set(cli, mockMainMenu);
        
        return cli;
    }
}
