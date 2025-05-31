package org.lucian.todos.cli.handler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.lucian.todos.cli.util.CLIUtils;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.exceptions.TodoNotFoundException;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;
import org.lucian.todos.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for todo-related CLI commands.
 * Processes user input for todo operations and displays results.
 * 

 */
public class TodoCommandHandler implements CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(TodoCommandHandler.class);
    private final TodoService todoService;
    private final Scanner scanner;
    
    public TodoCommandHandler(TodoService todoService, Scanner scanner) {
        this.todoService = todoService;
        this.scanner = scanner;
    }
    
    /**
     * Displays all todos.
     */
    public void showAllTodos() {
        try {
            List<Todo> todos = todoService.getAllTodos();
            if (todos.isEmpty()) {
                CLIUtils.printInfo("No todos found.");
                return;
            }
            
            CLIUtils.printHeader("All Todos");
            displayTodos(todos);
        } catch (DatabaseException e) {
            handleException("Failed to retrieve todos", e);
        }
    }
    
    /**
     * Lists all todos (alias for showAllTodos)
     */
    public void listTodos() {
        showAllTodos();
    }
    
    /**
     * Creates a new todo based on user input.
     */
    public void createTodo() {
        try {
            CLIUtils.printHeader("Create New Todo");
            
            String title = CLIUtils.getInput(scanner, "Enter todo title: ");
            if (title.trim().isEmpty()) {
                CLIUtils.printError("Todo title cannot be empty");
                return;
            }
            
            String description = CLIUtils.getInput(scanner, "Enter todo description (optional): ");
            
            LocalDate dueDate = null;
            String dueDateStr = CLIUtils.getInput(scanner, "Enter due date (YYYY-MM-DD, optional): ");
            if (!dueDateStr.trim().isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    CLIUtils.printError("Invalid date format. Using no due date.");
                }
            }
            
            Priority priority = promptForPriority();
            
            Todo todo = new Todo(title);
            todo.setDescription(description.isEmpty() ? null : description);
            todo.setDueDate(dueDate);
            todo.setPriority(priority);
            
            todo = todoService.createTodo(todo);
            
            CLIUtils.printSuccess("Todo created successfully with ID: " + todo.getId());
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            CLIUtils.printError("Invalid input: " + e.getMessage());
            logger.warn("Error creating todo", e);
        } catch (DatabaseException e) {
            handleException("Failed to create todo", e);
        }
    }
    
    /**
     * Updates an existing todo based on user input.
     */
    public void updateTodo() {
        try {
            CLIUtils.printHeader("Update Todo");
            
            Long id = promptForTodoId("Enter todo ID to update: ");
            if (id == null) return;
            
            Todo todo;
            try {
                todo = todoService.findTodoById(id);
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found with ID: " + id);
                return;
            }
            
            CLIUtils.printInfo("Current todo: " + todo.getTitle());
            
            String title = CLIUtils.getInputWithDefault(scanner, "Enter new title: ", todo.getTitle());
            String description = CLIUtils.getInputWithDefault(scanner, "Enter new description: ", 
                                                                  todo.getDescription() != null ? todo.getDescription() : "");
            
            LocalDate dueDate = todo.getDueDate();
            String dueDatePrompt = "Enter new due date (YYYY-MM-DD, empty to clear): ";
            String dueDateDefault = dueDate != null ? dueDate.toString() : "";
            String dueDateStr = CLIUtils.getInputWithDefault(scanner, dueDatePrompt, dueDateDefault);
            
            if (dueDateStr.trim().isEmpty()) {
                dueDate = null;
            } else {
                try {
                    dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    CLIUtils.printError("Invalid date format. Keeping current due date.");
                }
            }
            
            CLIUtils.printInfo("Current priority: " + todo.getPriority());
            Priority priority = promptForPriority();
            
            CLIUtils.printInfo("Current status: " + todo.getStatus());
            TodoStatus status = promptForStatus();
            
            todo.setTitle(title);
            todo.setDescription(description.isEmpty() ? null : description);
            todo.setDueDate(dueDate);
            todo.setPriority(priority);
            todo.setStatus(status);
            
            try {
                todoService.updateTodo(todo);
                CLIUtils.printSuccess("Todo updated successfully");
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found: " + e.getMessage());
            }
            
        } catch (IllegalArgumentException e) {
            CLIUtils.printError("Invalid input: " + e.getMessage());
        } catch (DatabaseException e) {
            handleException("Failed to update todo", e);
        }
    }
    
    /**
     * Deletes a todo based on user input.
     */
    public void deleteTodo() {
        try {
            CLIUtils.printHeader("Delete Todo");
            
            Long id = promptForTodoId("Enter todo ID to delete: ");
            if (id == null) return;
            
            try {
                Todo todo = todoService.findTodoById(id);
                CLIUtils.printInfo("Todo to delete: " + todo.getTitle());
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found with ID: " + id);
                return;
            }
            
            String confirm = CLIUtils.getInput(scanner, "Are you sure you want to delete this todo? (y/n): ");
            if (!confirm.equalsIgnoreCase("y")) {
                CLIUtils.printInfo("Deletion cancelled");
                return;
            }
            
            boolean deleted = todoService.deleteTodo(id);
            
            if (deleted) {
                CLIUtils.printSuccess("Todo deleted successfully");
            } else {
                CLIUtils.printError("Failed to delete todo");
            }
            
        } catch (DatabaseException e) {
            handleException("Failed to delete todo", e);
        }
    }
    
    /**
     * Displays a specific todo based on user input.
     */
    public void viewTodo() {
        try {
            CLIUtils.printHeader("View Todo");
            
            Long id = promptForTodoId("Enter todo ID to view: ");
            if (id == null) return;
            
            try {
                Todo todo = todoService.findTodoById(id);
                displayTodoDetails(todo);
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found with ID: " + id);
            }
            
        } catch (DatabaseException e) {
            handleException("Failed to retrieve todo", e);
        }
    }
    
    /**
     * Marks a todo as completed based on user input.
     */
    public void markTodoCompleted() {
        try {
            CLIUtils.printHeader("Mark Todo as Completed");
            
            Long id = promptForTodoId("Enter todo ID to mark as completed: ");
            if (id == null) return;
            
            try {
                Todo todo = todoService.markTodoCompleted(id);
                CLIUtils.printSuccess("Todo marked as completed: " + todo.getTitle());
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found with ID: " + id);
            } catch (IllegalStateException e) {
                CLIUtils.printError("Cannot complete todo: " + e.getMessage());
            }
            
        } catch (DatabaseException e) {
            handleException("Failed to update todo status", e);
        }
    }
    
    /**
     * Assigns a todo to a project based on user input.
     */
    public void assignTodoToProject() {
        try {
            CLIUtils.printHeader("Assign Todo to Project");
            
            Long todoId = promptForTodoId("Enter todo ID: ");
            if (todoId == null) return;
            
            String projectIdStr = CLIUtils.getInput(scanner, "Enter project ID (empty to remove from project): ");
            Long projectId = null;
            
            if (!projectIdStr.trim().isEmpty()) {
                try {
                    projectId = Long.valueOf(projectIdStr.trim());
                } catch (NumberFormatException e) {
                    CLIUtils.printError("Invalid project ID");
                    return;
                }
            }
            
            try {
                if (projectId == null) {
                    todoService.removeTodoFromProject(todoId);
                    CLIUtils.printSuccess("Todo removed from project");
                } else {
                    todoService.assignTodoToProject(todoId, projectId);
                    CLIUtils.printSuccess("Todo assigned to project " + projectId);
                }
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found with ID: " + todoId);
            }
            
        } catch (DatabaseException e) {
            handleException("Failed to update todo", e);
        }
    }
    
    /**
     * Displays overdue todos.
     */
    public void showOverdueTodos() {
        try {
            List<Todo> todos = todoService.getOverdueTodos();
            if (todos.isEmpty()) {
                CLIUtils.printInfo("No overdue todos found.");
                return;
            }
            
            CLIUtils.printHeader("Overdue Todos");
            displayTodos(todos);
        } catch (DatabaseException e) {
            handleException("Failed to retrieve overdue todos", e);
        }
    }
    
    /**
     * Displays todo statistics.
     */
    public void showTodoStatistics() {
        try {
            CLIUtils.printHeader("Todo Statistics");
            
            TodoService.TodoStatistics stats = todoService.getTodoStatistics();
            
            System.out.println("Total todos: " + stats.getTotalTodos());
            System.out.println("To-Do: " + stats.getTodoTodos());
            System.out.println("In Progress: " + stats.getInProgressTodos());
            System.out.println("Completed: " + stats.getCompletedTodos());
            System.out.println("Cancelled: " + stats.getCancelledTodos());
            System.out.println("Overdue: " + stats.getOverdueTodos());
            
        } catch (DatabaseException e) {
            handleException("Failed to retrieve todo statistics", e);
        }
    }
    
    /**
     * Searches for todos based on user input.
     */
    public void searchTodos() {
        try {
            CLIUtils.printHeader("Search Todos");
            
            String searchTerm = CLIUtils.getInput(scanner, "Enter search term: ");
            if (searchTerm.trim().isEmpty()) {
                CLIUtils.printError("Search term cannot be empty");
                return;
            }
            
            List<Todo> todos = todoService.searchTodos(searchTerm);
            
            if (todos.isEmpty()) {
                CLIUtils.printInfo("No todos found matching: " + searchTerm);
                return;
            }
            
            CLIUtils.printInfo("Found " + todos.size() + " todos matching: " + searchTerm);
            displayTodos(todos);
            
        } catch (IllegalArgumentException e) {
            CLIUtils.printError("Invalid input: " + e.getMessage());
        } catch (DatabaseException e) {
            handleException("Failed to search todos", e);
        }
    }
    
    /**
     * View todo details (alias for viewTodo)
     */
    public void viewTodoDetails() {
        viewTodo();
    }
    
    /**
     * Display todo statistics (alias for showTodoStatistics)
     */
    public void displayTodoStatistics() {
        showTodoStatistics();
    }
    
    /**
     * Update todo status based on user input.
     */
    public void updateTodoStatus() {
        try {
            CLIUtils.printHeader("Update Todo Status");
            
            Long id = promptForTodoId("Enter todo ID to update status: ");
            if (id == null) return;
            
            try {
                Todo todo = todoService.findTodoById(id);
                CLIUtils.printInfo("Current todo: " + todo.getTitle());
                CLIUtils.printInfo("Current status: " + todo.getStatus());
                
                TodoStatus newStatus = promptForStatus();
                
                todo = todoService.updateTodoStatus(id, newStatus);
                CLIUtils.printSuccess("Todo status updated to: " + todo.getStatus().getDisplayName());
            } catch (TodoNotFoundException e) {
                CLIUtils.printError("Todo not found with ID: " + id);
            }
        } catch (DatabaseException e) {
            handleException("Failed to update todo status", e);
        }
    }
    
    /**
     * Display overdue todos.
     */
    public void displayOverdueTodos() {
        try {
            List<Todo> todos = todoService.getOverdueTodos();
            if (todos.isEmpty()) {
                CLIUtils.printInfo("No overdue todos found.");
                return;
            }
            
            CLIUtils.printHeader("Overdue Todos");
            displayTodos(todos);
        } catch (DatabaseException e) {
            handleException("Failed to retrieve overdue todos", e);
        }
    }
    
    /**
     * Display todos by priority.
     */
    public void displayTodosByPriority() {
        try {
            CLIUtils.printHeader("Todos by Priority");
            
            System.out.println("Select priority to display:");
            System.out.println("1. Urgent");
            System.out.println("2. High");
            System.out.println("3. Medium");
            System.out.println("4. Low");
            System.out.println("5. All priorities");
            
            String input = CLIUtils.getInput(scanner, "Enter choice (1-5): ");
            
            Priority priority = null;
            String priorityName = "All";
            
            switch (input.trim()) {
                case "1":
                    priority = Priority.URGENT;
                    priorityName = "Urgent";
                    break;
                case "2":
                    priority = Priority.HIGH;
                    priorityName = "High";
                    break;
                case "3":
                    priority = Priority.MEDIUM;
                    priorityName = "Medium";
                    break;
                case "4":
                    priority = Priority.LOW;
                    priorityName = "Low";
                    break;
                case "5":
                default:
                    // All priorities
                    break;
            }
            
            List<Todo> todos;
            if (priority != null) {
                todos = todoService.getTodosByPriority(priority);
                CLIUtils.printInfo("Todos with " + priorityName + " Priority:");
            } else {
                todos = todoService.getAllTodos();
                CLIUtils.printInfo("All Todos by Priority:");
            }
            
            if (todos.isEmpty()) {
                CLIUtils.printInfo("No todos found with the selected priority.");
                return;
            }
            
            // Group and sort by priority if showing all
            if (priority == null) {
                todos.sort((t1, t2) -> t2.getPriority().compareTo(t1.getPriority()));
            }
            
            displayTodos(todos);
            
        } catch (DatabaseException e) {
            handleException("Failed to retrieve todos by priority", e);
        }
    }
    
    /**
     * Displays todos in a formatted table.
     */
    private void displayTodos(List<Todo> todos) {
        String format = "| %-4s | %-30s | %-12s | %-12s | %-12s |%n";
        
        System.out.format("+------+--------------------------------+--------------+--------------+--------------+%n");
        System.out.format("| ID   | Title                          | Due Date     | Priority     | Status       |%n");
        System.out.format("+------+--------------------------------+--------------+--------------+--------------+%n");
        
        for (Todo todo : todos) {
            String dueDate = todo.getDueDate() != null ? todo.getDueDate().toString() : "None";
            System.out.format(format, 
                             todo.getId(), 
                             truncate(todo.getTitle(), 30),
                             dueDate,
                             todo.getPriority(),
                             todo.getStatus());
        }
        
        System.out.format("+------+--------------------------------+--------------+--------------+--------------+%n");
    }
    
    /**
     * Displays detailed information about a todo.
     */
    private void displayTodoDetails(Todo todo) {
        CLIUtils.printHeader("Todo Details: " + todo.getTitle());
        
        System.out.println("ID: " + todo.getId());
        System.out.println("Title: " + todo.getTitle());
        System.out.println("Description: " + (todo.getDescription() != null ? todo.getDescription() : "None"));
        System.out.println("Due Date: " + (todo.getDueDate() != null ? todo.getDueDate() : "None"));
        System.out.println("Priority: " + todo.getPriority());
        System.out.println("Status: " + todo.getStatus());
        System.out.println("Project ID: " + (todo.getProjectId() != null ? todo.getProjectId() : "None"));
        System.out.println("Created At: " + todo.getCreatedAt());
        
        if (todo.isOverdue()) {
            System.out.println("Overdue: YES");
        }
    }
    
    /**
     * Prompts the user to enter a todo ID.
     */
    private Long promptForTodoId(String prompt) {
        String idStr = CLIUtils.getInput(scanner, prompt);
        
        if (idStr.trim().isEmpty()) {
            CLIUtils.printError("Todo ID cannot be empty");
            return null;
        }
        
        try {
            return Long.valueOf(idStr.trim());
        } catch (NumberFormatException e) {
            CLIUtils.printError("Invalid todo ID format");
            return null;
        }
    }
    
    /**
     * Prompts the user to select a priority.
     */
    private Priority promptForPriority() {
        System.out.println("Select priority:");
        System.out.println("1. Low");
        System.out.println("2. Medium (default)");
        System.out.println("3. High");
        System.out.println("4. Urgent");
        
        String input = CLIUtils.getInput(scanner, "Enter priority (1-4): ");
        
        return switch (input.trim()) {
            case "1" -> Priority.LOW;
            case "3" -> Priority.HIGH;
            case "4" -> Priority.URGENT;
            default -> Priority.MEDIUM;
        };
    }
    
    /**
     * Prompts the user to select a status.
     */
    private TodoStatus promptForStatus() {
        System.out.println("Select status:");
        System.out.println("1. To Do (default)");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");
        System.out.println("4. Cancelled");
        
        String input = CLIUtils.getInput(scanner, "Enter status (1-4): ");
        
        return switch (input.trim()) {
            case "2" -> TodoStatus.IN_PROGRESS;
            case "3" -> TodoStatus.COMPLETED;
            case "4" -> TodoStatus.CANCELLED;
            default -> TodoStatus.TODO;
        };
    }
    
    /**
     * Truncates a string to a specific length.
     */
    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Handles exceptions in a user-friendly way.
     */
    private void handleException(String message, Exception e) {
        CLIUtils.printError(message + ": " + e.getMessage());
        logger.error(message, e);
    }
}