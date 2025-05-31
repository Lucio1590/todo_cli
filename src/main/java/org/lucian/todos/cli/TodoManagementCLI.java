package org.lucian.todos.cli;

import java.util.Scanner;

import org.lucian.todos.cli.handler.AuthenticationCommandHandler;
import org.lucian.todos.cli.handler.ProjectCommandHandler;
import org.lucian.todos.cli.handler.TodoCommandHandler;
import org.lucian.todos.cli.menu.MainMenu;
import org.lucian.todos.cli.util.CLIUtils;
import org.lucian.todos.dao.DAOFactory;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.service.AuthenticationService;
import org.lucian.todos.service.ProjectService;
import org.lucian.todos.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main CLI controller for the Todo Management System.
 * Handles application initialization, service setup, and main menu execution.
 * 

 */
public class TodoManagementCLI {
    
    private static final Logger logger = LoggerFactory.getLogger(TodoManagementCLI.class);
    
    private final Scanner scanner;
    private final TodoService todoService;
    private final ProjectService projectService;
    private final AuthenticationService authService;
    private final AuthenticationCommandHandler authHandler;
    private final MainMenu mainMenu;
    private boolean running;
    
    /**
     * Constructs a new TodoManagementCLI instance.
     * Initializes services and dependencies.
     */
    public TodoManagementCLI() {
        this.scanner = new Scanner(System.in);
        
        // Initialize database and services
        try {
            DAOFactory daoFactory = DAOFactory.getInstance();
            this.authService = new AuthenticationService(daoFactory.getUserDAO());
            this.todoService = new TodoService(daoFactory.getTodoDAO(), authService);
            this.projectService = new ProjectService(daoFactory.getProjectDAO(), daoFactory.getTodoDAO());
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
        
        // Initialize command handlers
        this.authHandler = new AuthenticationCommandHandler(authService, scanner);
        TodoCommandHandler todoCommandHandler = new TodoCommandHandler(todoService, scanner);
        ProjectCommandHandler projectCommandHandler = new ProjectCommandHandler(projectService, todoService, scanner);
        
        // Initialize main menu
        this.mainMenu = new MainMenu(scanner, todoCommandHandler, projectCommandHandler, authHandler, authService);
        this.running = true;
        
        logger.info("Todo Management CLI initialized successfully");
    }
    
    /**
     * Starts the CLI application.
     * Displays welcome message and handles authentication flow.
     */
    public void start() {
        displayWelcome();
        
        try {
            // Authentication flow - user must log in before accessing main system
            if (!handleAuthenticationFlow()) {
                CLIUtils.printInfo("Goodbye!");
                return;
            }
            
            // Main application loop - only accessible after authentication
            while (running) {
                mainMenu.display();
                handleMainMenuChoice();
            }
        } catch (Exception e) {
            logger.error("Unexpected error in CLI", e);
            CLIUtils.printError("An unexpected error occurred: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Handles the main menu choice from user input.
     */
    private void handleMainMenuChoice() {
        try {
            String choice = CLIUtils.getInput(scanner, "Enter your choice: ").trim();
            
            switch (choice.toLowerCase()) {
                case "1", "todos" -> mainMenu.handleTodoMenu();
                case "2", "projects" -> mainMenu.handleProjectMenu();
                case "3", "stats", "statistics" -> mainMenu.handleStatisticsMenu();
                case "4", "account", "profile" -> mainMenu.handleAccountMenu();
                case "5", "help" -> mainMenu.displayHelp();
                case "6", "exit", "quit", "q" -> confirmExit();
                default -> {
                    CLIUtils.printError("Invalid choice. Please try again.");
                    CLIUtils.waitForKeyPress(scanner);
                }
            }
        } catch (Exception e) {
            logger.error("Error handling menu choice", e);
            CLIUtils.printError("Error processing your request: " + e.getMessage());
            CLIUtils.waitForKeyPress(scanner);
        }
    }
    
    /**
     * Displays the welcome message and application information.
     */
    private void displayWelcome() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Todo Management System");
        
        System.out.println("Welcome to the Todo Management System!");
        System.out.println("A comprehensive solution for managing your todos and projects.");
        System.out.println();
        
        // Display quick stats
        try {
            var todoStats = todoService.getTodoStatistics();
            var projectStats = projectService.getProjectStatistics();
            
            System.out.println("Current System Status:");
            System.out.printf("  • Todos: %d total (%d pending, %d completed)%n", 
                todoStats.getTotalTodos(), 
                todoStats.getTodoTodos() + todoStats.getInProgressTodos(),
                todoStats.getCompletedTodos());
            System.out.printf("  • Projects: %d total (%d active, %d completed)%n", 
                projectStats.getTotalProjects(), 
                projectStats.getActiveProjects(),
                projectStats.getCompletedProjects());
            
            if (todoStats.getOverdueTodos() > 0) {
                CLIUtils.printWarning("⚠ Warning: You have " + todoStats.getOverdueTodos() + " overdue todos!");
            }
        } catch (DatabaseException | IllegalArgumentException | IllegalStateException e) {
            logger.warn("Could not load system statistics: {}", e.getMessage());
            System.out.println("  • System ready for use");
        } catch (RuntimeException e) {
            logger.warn("Runtime error while loading statistics", e);
            System.out.println("  • System ready for use");
        }
        
        System.out.println();
        CLIUtils.printSuccess("System initialized successfully!");
        System.out.println();
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Confirms exit with the user and handles graceful shutdown.
     */
    private void confirmExit() {
        System.out.println();
        String confirm = CLIUtils.getInput(scanner, "Are you sure you want to exit? (y/N): ");
        
        if (confirm.toLowerCase().startsWith("y")) {
            running = false;
            CLIUtils.printSuccess("Thank you for using Todo Management System!");
            System.out.println("Goodbye!");
        }
    }
    
    /**
     * Cleans up resources before application shutdown.
     */
    private void cleanup() {
        try {
            if (scanner != null) {
                scanner.close();
            }
            logger.info("Todo Management CLI shutting down");
        } catch (Exception e) {
            logger.error("Error during cleanup", e);
        }
    }
    
    /**
     * Handles the authentication flow.
     * Users must login or register before accessing the main system.
     * 
     * @return true if user successfully authenticated, false if they chose to exit
     */
    private boolean handleAuthenticationFlow() {
        if (!authService.isLoggedIn()) {
            // Use the authentication handler's login method
            return authHandler.handleLogin();
        }
        return true; // Already logged in
    }
}
