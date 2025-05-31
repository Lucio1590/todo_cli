package org.lucian.todos.cli.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.lucian.todos.model.Priority;
import org.lucian.todos.model.Project;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;

/**
 * Utility class for CLI operations including formatting, input handling, and display.
 * 

 */
public class CLIUtils {
    
    // ANSI Color codes for terminal formatting
    public static final String RESET = "\033[0m";
    public static final String BOLD = "\033[1m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String MAGENTA = "\033[35m";
    public static final String CYAN = "\033[36m";
    
    // Date formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    /**
     * Clears the console screen.
     */
    public static void clearScreen() {
        try {
            // Try to clear screen on different operating systems
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[2J\033[H");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            // If clearing fails, just print some empty lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Prints a header with formatting.
     * 
     * @param title the header title
     */
    public static void printHeader(String title) {
        String border = "=".repeat(Math.max(50, title.length() + 10));
        System.out.println(CYAN + border + RESET);
        System.out.println(CYAN + BOLD + centerText(title, border.length()) + RESET);
        System.out.println(CYAN + border + RESET);
        System.out.println();
    }
    
    /**
     * Prints a section header.
     * 
     * @param title the section title
     */
    public static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(BLUE + BOLD + title + RESET);
        System.out.println(BLUE + "-".repeat(title.length()) + RESET);
    }
    
    /**
     * Prints a success message in green.
     * 
     * @param message the success message
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }
    
    /**
     * Prints an error message in red.
     * 
     * @param message the error message
     */
    public static void printError(String message) {
        System.out.println(RED + "✗ " + message + RESET);
    }
    
    /**
     * Prints a warning message in yellow.
     * 
     * @param message the warning message
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }
    
    /**
     * Prints an info message in blue.
     * 
     * @param message the info message
     */
    public static void printInfo(String message) {
        System.out.println(BLUE + "ℹ " + message + RESET);
    }
    
    /**
     * Gets input from the user with a prompt.
     * 
     * @param scanner the scanner instance
     * @param prompt the input prompt
     * @return the user input
     */
    public static String getInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    /**
     * Gets input from the user with a prompt and default value.
     * 
     * @param scanner the scanner instance
     * @param prompt the input prompt
     * @param defaultValue the default value if input is empty
     * @return the user input or default value
     */
    public static String getInputWithDefault(Scanner scanner, String prompt, String defaultValue) {
        System.out.printf("%s [%s]: ", prompt, defaultValue);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
    
    /**
     * Gets a valid integer input from the user.
     * 
     * @param scanner the scanner instance
     * @param prompt the input prompt
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return the valid integer input
     */
    public static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                String input = getInput(scanner, prompt);
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    printError(String.format("Please enter a number between %d and %d", min, max));
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number");
            }
        }
    }
    
    /**
     * Gets a valid date input from the user.
     * 
     * @param scanner the scanner instance
     * @param prompt the input prompt
     * @param allowEmpty whether empty input is allowed
     * @return the parsed date or null if empty and allowed
     */
    public static LocalDate getDateInput(Scanner scanner, String prompt, boolean allowEmpty) {
        while (true) {
            String input = getInput(scanner, prompt + " (yyyy-MM-dd)" + (allowEmpty ? " [optional]" : "") + ": ");
            
            if (allowEmpty && input.trim().isEmpty()) {
                return null;
            }
            
            try {
                return LocalDate.parse(input.trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                printError("Please enter a valid date in format: yyyy-MM-dd (e.g., 2024-12-25)");
            }
        }
    }
    
    /**
     * Gets a valid priority input from the user.
     * 
     * @param scanner the scanner instance
     * @param prompt the input prompt
     * @return the selected priority
     */
    public static Priority getPriorityInput(Scanner scanner, String prompt) {
        System.out.println("\nAvailable priorities:");
        for (int i = 0; i < Priority.values().length; i++) {
            Priority priority = Priority.values()[i];
            System.out.printf("  %d. %s%n", i + 1, priority.toString());
        }
        
        int choice = getIntInput(scanner, prompt + " (1-" + Priority.values().length + "): ", 
                                1, Priority.values().length);
        return Priority.values()[choice - 1];
    }
    
    /**
     * Gets a valid todo status input from the user.
     * 
     * @param scanner the scanner instance
     * @param prompt the input prompt
     * @return the selected todo status
     */
    public static TodoStatus getTodoStatusInput(Scanner scanner, String prompt) {
        System.out.println("\nAvailable statuses:");
        for (int i = 0; i < TodoStatus.values().length; i++) {
            TodoStatus status = TodoStatus.values()[i];
            System.out.printf("  %d. %s%n", i + 1, status.getDisplayName());
        }
        
        int choice = getIntInput(scanner, prompt + " (1-" + TodoStatus.values().length + "): ", 
                                1, TodoStatus.values().length);
        return TodoStatus.values()[choice - 1];
    }
    
    /**
     * Waits for the user to press Enter to continue.
     * 
     * @param scanner the scanner instance
     */
    public static void waitForKeyPress(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Centers text within a given width.
     * 
     * @param text the text to center
     * @param width the total width
     * @return the centered text
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
    
    /**
     * Formats a todo for display.
     * 
     * @param todo the todo to format
     * @return formatted todo string
     */
    public static String formatTodo(Todo todo) {
        StringBuilder sb = new StringBuilder();
        
        // Todo ID and title
        sb.append(String.format("%s[ID: %d]%s %s%s%s", 
            CYAN, todo.getId(), RESET,
            BOLD, todo.getTitle(), RESET));
        
        // Status with color coding
        String statusColor = getStatusColor(todo.getStatus());
        sb.append(String.format(" - %s%s%s", statusColor, todo.getStatus().getDisplayName(), RESET));
        
        // Priority with color coding
        String priorityColor = getPriorityColor(todo.getPriority());
        sb.append(String.format(" [%s%s%s]", priorityColor, todo.getPriority().toString(), RESET));
        
        // Due date
        if (todo.getDueDate() != null) {
            String dueDateStr = todo.getDueDate().format(DISPLAY_DATE_FORMATTER);
            if (todo.getDueDate().isBefore(LocalDate.now()) && !todo.getStatus().isCompleted()) {
                sb.append(String.format(" %s(Due: %s - OVERDUE!)%s", RED, dueDateStr, RESET));
            } else {
                sb.append(String.format(" (Due: %s)", dueDateStr));
            }
        }
        
        // Project assignment
        if (todo.getProjectId() != null) {
            sb.append(String.format(" %s[Project ID: %d]%s", MAGENTA, todo.getProjectId(), RESET));
        }
        
        // Description (if available and not too long)
        if (todo.getDescription() != null && !todo.getDescription().trim().isEmpty()) {
            String desc = todo.getDescription().length() > 50 
                ? todo.getDescription().substring(0, 47) + "..."
                : todo.getDescription();
            sb.append(String.format("%n    %s", desc));
        }
        
        return sb.toString();
    }
    
    /**
     * Formats a project for display.
     * 
     * @param project the project to format
     * @return formatted project string
     */
    public static String formatProject(Project project) {
        StringBuilder sb = new StringBuilder();
        
        // Project ID and name
        sb.append(String.format("%s[ID: %d]%s %s%s%s", 
            CYAN, project.getId(), RESET,
            BOLD, project.getName(), RESET));
        
        // Dates
        if (project.getStartDate() != null) {
            sb.append(String.format(" (Started: %s", project.getStartDate().format(DISPLAY_DATE_FORMATTER)));
            if (project.getEndDate() != null) {
                sb.append(String.format(" - %s)", project.getEndDate().format(DISPLAY_DATE_FORMATTER)));
            } else {
                sb.append(")");
            }
        }
        
        // Todo count
        int todoCount = project.getTodos().size();
        if (todoCount > 0) {
            sb.append(String.format(" %s[%d todos]%s", MAGENTA, todoCount, RESET));
        }
        
        // Description
        if (project.getDescription() != null && !project.getDescription().trim().isEmpty()) {
            String desc = project.getDescription().length() > 60 
                ? project.getDescription().substring(0, 57) + "..."
                : project.getDescription();
            sb.append(String.format("%n    %s", desc));
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the color code for a todo status.
     * 
     * @param status the todo status
     * @return the ANSI color code
     */
    private static String getStatusColor(TodoStatus status) {
        return switch (status) {
            case TODO -> YELLOW;
            case IN_PROGRESS -> BLUE;
            case COMPLETED -> GREEN;
            case CANCELLED -> RED;
            default -> RESET;
        };
    }
    
    /**
     * Gets the color code for a priority.
     * 
     * @param priority the priority
     * @return the ANSI color code
     */
    private static String getPriorityColor(Priority priority) {
        return switch (priority) {
            case URGENT -> RED + BOLD;
            case HIGH -> RED;
            case MEDIUM -> YELLOW;
            case LOW -> GREEN;
            default -> RESET;
        };
    }
    
    /**
     * Displays a paginated list of items.
     * 
     * @param items the list of items to display
     * @param itemsPerPage number of items per page
     * @param scanner the scanner for user input
     * @param formatter function to format each item
     */
    public static <T> void displayPaginatedList(List<T> items, int itemsPerPage, Scanner scanner, 
                                                java.util.function.Function<T, String> formatter) {
        if (items.isEmpty()) {
            printInfo("No items to display.");
            return;
        }
        
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        int currentPage = 1;
        
        while (true) {
            clearScreen();
            printSectionHeader(String.format("Items (Page %d of %d)", currentPage, totalPages));
            
            int startIndex = (currentPage - 1) * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, items.size());
            
            for (int i = startIndex; i < endIndex; i++) {
                System.out.printf("%d. %s%n", i + 1, formatter.apply(items.get(i)));
            }
            
            System.out.println();
            System.out.printf("Showing %d-%d of %d items%n", startIndex + 1, endIndex, items.size());
            
            if (totalPages > 1) {
                System.out.println();
                System.out.println("Navigation: [n]ext, [p]revious, [q]uit");
                String choice = getInput(scanner, "Choice: ").toLowerCase();
                
                switch (choice) {
                    case "n", "next" -> {
                        if (currentPage < totalPages) currentPage++;
                    }
                    case "p", "prev", "previous" -> {
                        if (currentPage > 1) currentPage--;
                    }
                    case "q", "quit" -> {
                        return;
                    }
                    default -> {
                        printError("Invalid choice. Press Enter to continue...");
                        scanner.nextLine();
                    }
                }
            } else {
                waitForKeyPress(scanner);
                break;
            }
        }
    }
    
    /**
     * Formats a date for display.
     * 
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE_FORMATTER) : "Not set";
    }
    
    /**
     * Formats a datetime for display.
     * 
     * @param dateTime the datetime to format
     * @return formatted datetime string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_DATETIME_FORMATTER) : "Not set";
    }
}
