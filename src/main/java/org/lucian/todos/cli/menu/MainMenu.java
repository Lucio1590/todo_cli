package org.lucian.todos.cli.menu;

import java.util.Scanner;

import org.lucian.todos.cli.handler.AuthenticationCommandHandler;
import org.lucian.todos.cli.handler.ProjectCommandHandler;
import org.lucian.todos.cli.handler.TodoCommandHandler;
import org.lucian.todos.cli.util.CLIUtils;
import org.lucian.todos.cli.util.ErrorHandler;
import org.lucian.todos.model.User;
import org.lucian.todos.service.AuthenticationService;

/**
 * Main menu system for the Todo Management CLI.
 * Handles menu display and navigation between different functional areas.
 * 

 */
public class MainMenu {
    
    private final Scanner scanner;
    private final TodoCommandHandler todoHandler;
    private final ProjectCommandHandler projectHandler;
    private final AuthenticationCommandHandler authHandler;
    private final AuthenticationService authService;
    
    /**
     * Constructs a new MainMenu instance.
     * 
     * @param scanner the scanner for user input
     * @param todoHandler the todo command handler
     * @param projectHandler the project command handler
     * @param authHandler the authentication command handler
     * @param authService the authentication service
     */
    public MainMenu(Scanner scanner, TodoCommandHandler todoHandler, ProjectCommandHandler projectHandler, 
                   AuthenticationCommandHandler authHandler, AuthenticationService authService) {
        this.scanner = scanner;
        this.todoHandler = todoHandler;
        this.projectHandler = projectHandler;
        this.authHandler = authHandler;
        this.authService = authService;
    }
    
    /**
     * Displays the main menu.
     */
    public void display() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Todo Management System - Main Menu");
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                System.out.println("Logged in as: " + CLIUtils.BOLD + currentUser.getFirstName() + " " + 
                                 currentUser.getLastName() + CLIUtils.RESET + " (" + currentUser.getUsername() + ")");
                System.out.println();
            }
        } catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, "displaying current user info");
        }
        
        System.out.println("Please select an option:");
        System.out.println();
        System.out.println("  1. Todo Management");
        System.out.println("     ├─ Create, view, edit, and delete todos");
        System.out.println("     ├─ Update todo status and priorities");
        System.out.println("     └─ Manage todo assignments");
        System.out.println();
        System.out.println("  2. Project Management");
        System.out.println("     ├─ Create, view, edit, and delete projects");
        System.out.println("     ├─ Assign todos to projects");
        System.out.println("     └─ Track project progress");
        System.out.println();
        System.out.println("  3. Statistics & Reports");
        System.out.println("     ├─ View todo and project statistics");
        System.out.println("     ├─ Generate progress reports");
        System.out.println("     └─ Analyze productivity metrics");
        System.out.println();
        System.out.println("  4. Account Management");
        System.out.println("     ├─ View and update profile");
        System.out.println("     ├─ Change password");
        System.out.println("     └─ Logout");
        System.out.println();
        System.out.println("  5. Help & Documentation");
        System.out.println("     └─ View usage instructions and tips");
        System.out.println();
        System.out.println("  6. Exit");
        System.out.println("     └─ Save and quit the application");
        System.out.println();
        
        CLIUtils.printInfo("Tip: You can type the number or the name (e.g., 'todos', 'projects', 'account')");
        System.out.println();
    }
    
    /**
     * Handles the todo management menu.
     */
    public void handleTodoMenu() {
        while (true) {
            displayTodoMenu();
            String choice = CLIUtils.getInput(scanner, "Enter your choice: ").trim();
            
            try {
                switch (choice.toLowerCase()) {
                    case "1", "create" -> todoHandler.createTodo();
                    case "2", "list", "view" -> todoHandler.listTodos();
                    case "3", "edit", "update" -> todoHandler.updateTodo();
                    case "4", "delete", "remove" -> todoHandler.deleteTodo();
                    case "5", "status" -> todoHandler.updateTodoStatus();
                    case "6", "search", "find" -> todoHandler.searchTodos();
                    case "7", "assign" -> todoHandler.assignTodoToProject();
                    case "8", "details" -> todoHandler.viewTodoDetails();
                    case "9", "back", "return" -> {
                        return;
                    }
                    default -> {
                        CLIUtils.printError("Invalid choice. Please try again.");
                        CLIUtils.waitForKeyPress(scanner);
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handleUnexpectedError(e, "handling todo menu");
                CLIUtils.waitForKeyPress(scanner);
            }
        }
    }
    
    /**
     * Displays the todo management submenu.
     */
    private void displayTodoMenu() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Todo Management");
        
        System.out.println("Todo Operations:");
        System.out.println();
        System.out.println("  1. Create New Todo");
        System.out.println("  2. List All Todos");
        System.out.println("  3. Edit Todo");
        System.out.println("  4. Delete Todo");
        System.out.println("  5. Update Todo Status");
        System.out.println("  6. Search Todos");
        System.out.println("  7. Assign Todo to Project");
        System.out.println("  8. View Todo Details");
        System.out.println("  9. Back to Main Menu");
        System.out.println();
    }
    
    /**
     * Handles the project management menu.
     */
    public void handleProjectMenu() {
        while (true) {
            displayProjectMenu();
            String choice = CLIUtils.getInput(scanner, "Enter your choice: ").trim();
            
            try {
                switch (choice.toLowerCase()) {
                    case "1", "create" -> projectHandler.createProject();
                    case "2", "list", "view" -> projectHandler.listProjects();
                    case "3", "edit", "update" -> projectHandler.updateProject();
                    case "4", "delete", "remove" -> projectHandler.deleteProject();
                    case "5", "todos" -> projectHandler.viewProjectTodos();
                    case "6", "assign" -> projectHandler.assignTodoToProject();
                    case "7", "progress", "stats" -> projectHandler.viewProjectProgress();
                    case "8", "details" -> projectHandler.viewProjectDetails();
                    case "9", "back", "return" -> {
                        return;
                    }
                    default -> {
                        CLIUtils.printError("Invalid choice. Please try again.");
                        CLIUtils.waitForKeyPress(scanner);
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handleUnexpectedError(e, "handling project menu");
                CLIUtils.waitForKeyPress(scanner);
            }
        }
    }
    
    /**
     * Displays the project management submenu.
     */
    private void displayProjectMenu() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Project Management");
        
        System.out.println("Project Operations:");
        System.out.println();
        System.out.println("  1. Create New Project");
        System.out.println("  2. List All Projects");
        System.out.println("  3. Edit Project");
        System.out.println("  4. Delete Project");
        System.out.println("  5. View Project Todos");
        System.out.println("  6. Assign Todo to Project");
        System.out.println("  7. View Project Progress");
        System.out.println("  8. View Project Details");
        System.out.println("  9. Back to Main Menu");
        System.out.println();
    }
    
    /**
     * Handles the statistics and reports menu.
     */
    public void handleStatisticsMenu() {
        while (true) {
            displayStatisticsMenu();
            String choice = CLIUtils.getInput(scanner, "Enter your choice: ").trim();
            
            try {
                switch (choice.toLowerCase()) {
                    case "1", "todo", "todos" -> todoHandler.displayTodoStatistics();
                    case "2", "project", "projects" -> projectHandler.displayProjectStatistics();
                    case "3", "overdue" -> todoHandler.displayOverdueTodos();
                    case "4", "priority" -> todoHandler.displayTodosByPriority();
                    case "5", "completion" -> projectHandler.displayProjectCompletionStats();
                    case "6", "summary" -> displaySystemSummary();
                    case "7", "back", "return" -> {
                        return;
                    }
                    default -> {
                        CLIUtils.printError("Invalid choice. Please try again.");
                        CLIUtils.waitForKeyPress(scanner);
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handleUnexpectedError(e, "handling statistics menu");
                CLIUtils.waitForKeyPress(scanner);
            }
        }
    }
    
    /**
     * Displays the statistics and reports submenu.
     */
    private void displayStatisticsMenu() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Statistics & Reports");
        
        System.out.println("Available Reports:");
        System.out.println();
        System.out.println("  1. Todo Statistics");
        System.out.println("  2. Project Statistics");
        System.out.println("  3. Overdue Todos Report");
        System.out.println("  4. Todos by Priority");
        System.out.println("  5. Project Completion Stats");
        System.out.println("  6. System Summary");
        System.out.println("  7. Back to Main Menu");
        System.out.println();
    }
    
    /**
     * Displays a comprehensive system summary.
     */
    private void displaySystemSummary() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("System Summary");
        
        try {
            // Display both todo and project statistics
            todoHandler.displayTodoStatistics();
            System.out.println();
            projectHandler.displayProjectStatistics();
            
        } catch (Exception e) {
            CLIUtils.printError("Error generating system summary: " + e.getMessage());
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Displays help and documentation.
     */
    public void displayHelp() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Help & Documentation");
        
        System.out.println("Welcome to the Todo Management System Help!");
        System.out.println();
        
        CLIUtils.printSectionHeader("Getting Started");
        System.out.println("• Use the main menu to navigate between different areas");
        System.out.println("• You can type numbers or keywords (e.g., 'todos', 'projects')");
        System.out.println("• Follow the prompts to enter information");
        System.out.println("• Use 'back' or 'return' to go back to previous menus");
        
        CLIUtils.printSectionHeader("Todo Management");
        System.out.println("• Create todos with titles, descriptions, priorities, and due dates");
        System.out.println("• Todos can be TODO, IN_PROGRESS, COMPLETED, or CANCELLED");
        System.out.println("• Priorities range from LOW to URGENT");
        System.out.println("• Assign todos to projects for better organization");
        
        CLIUtils.printSectionHeader("Project Management");
        System.out.println("• Create projects to group related todos");
        System.out.println("• Set start and end dates for projects");
        System.out.println("• Track progress by viewing project completion statistics");
        System.out.println("• Assign multiple todos to a single project");
        
        CLIUtils.printSectionHeader("Tips & Tricks");
        System.out.println("• Use the statistics menu to get insights into your productivity");
        System.out.println("• Regularly check the overdue todos report");
        System.out.println("• Set realistic due dates and priorities for better todo management");
        System.out.println("• Use projects to organize todos by theme, deadline, or complexity");
        
        CLIUtils.printSectionHeader("Keyboard Shortcuts");
        System.out.println("• Type 'q' or 'quit' to exit from most menus");
        System.out.println("• Use 'back' to return to previous menu");
        System.out.println("• Press Enter when prompted to continue");
        
        System.out.println();
        CLIUtils.printSuccess("For more help, refer to the user manual or contact support.");
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Handles the account management menu.
     */
    public void handleAccountMenu() {
        try {
            authHandler.handleAuthenticatedMenu();
        } catch (Exception e) {
            ErrorHandler.handleUnexpectedError(e, "handling account menu");
            CLIUtils.waitForKeyPress(scanner);
        }
    }
}
