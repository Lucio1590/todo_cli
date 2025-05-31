package org.lucian.todos.cli.handler;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.lucian.todos.cli.util.CLIUtils;
import org.lucian.todos.exceptions.ProjectNotFoundException;
import org.lucian.todos.exceptions.TodoManagementException;
import org.lucian.todos.model.Project;
import org.lucian.todos.model.Todo;
import org.lucian.todos.service.ProjectService;
import org.lucian.todos.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command handler for project-related operations in the CLI.
 * Handles user input and delegates to the ProjectService for business logic.
 * 

 */
public class ProjectCommandHandler implements CommandHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectCommandHandler.class);
    private static final int PROJECTS_PER_PAGE = 8;
    
    private final ProjectService projectService;
    private final TodoService todoService;
    private final Scanner scanner;
    
    /**
     * Constructs a new ProjectCommandHandler.
     * 
     * @param projectService the project service
     * @param todoService the todo service
     * @param scanner the scanner for user input
     */
    public ProjectCommandHandler(ProjectService projectService, TodoService todoService, Scanner scanner) {
        this.projectService = projectService;
        this.todoService = todoService;
        this.scanner = scanner;
    }
    
    /**
     * Creates a new project through user interaction.
     */
    public void createProject() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Create New Project");
        
        try {
            // Get project details from user
            String name = CLIUtils.getInput(scanner, "Project name: ").trim();
            if (name.isEmpty()) {
                CLIUtils.printError("Project name cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            String description = CLIUtils.getInput(scanner, "Project description (optional): ").trim();
            if (description.isEmpty()) {
                description = null;
            }
            
            LocalDate startDate = CLIUtils.getDateInput(scanner, "Start date", true);
            LocalDate endDate = CLIUtils.getDateInput(scanner, "End date", true);
            
            // Validate dates
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                CLIUtils.printError("End date cannot be before start date.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Create project
            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
            project.setStartDate(startDate);
            project.setEndDate(endDate);
            
            Project createdProject = projectService.createProject(project);
            CLIUtils.printSuccess("Project created successfully!");
            System.out.println("\nCreated project:");
            System.out.println(CLIUtils.formatProject(createdProject));
            
        } catch (TodoManagementException e) {
            CLIUtils.printError("Failed to create project: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating project", e);
            CLIUtils.printError("An unexpected error occurred while creating the project.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Lists all projects with pagination.
     */
    public void listProjects() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Project List");
        
        try {
            List<Project> projects = projectService.getAllProjects();
            
            if (projects.isEmpty()) {
                CLIUtils.printInfo("No projects found. Create your first project to get started!");
            } else {
                CLIUtils.displayPaginatedList(projects, PROJECTS_PER_PAGE, scanner, CLIUtils::formatProject);
            }
            
        } catch (TodoManagementException e) {
            CLIUtils.printError("Failed to retrieve projects: " + e.getMessage());
            CLIUtils.waitForKeyPress(scanner);
        } catch (Exception e) {
            logger.error("Unexpected error listing projects", e);
            CLIUtils.printError("An unexpected error occurred while retrieving projects.");
            CLIUtils.waitForKeyPress(scanner);
        }
    }
    
    /**
     * Updates an existing project.
     */
    public void updateProject() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Update Project");
        
        try {
            Long projectId = (long) CLIUtils.getIntInput(scanner, "Enter project ID to update: ", 1, Integer.MAX_VALUE);
            
            Project existingProject = projectService.getProjectById(projectId);
            System.out.println("\nCurrent project details:");
            System.out.println(CLIUtils.formatProject(existingProject));
            System.out.println();
            
            // Update fields
            String newName = CLIUtils.getInputWithDefault(scanner, "New name", existingProject.getName());
            String newDescription = CLIUtils.getInputWithDefault(scanner, "New description", 
                existingProject.getDescription() != null ? existingProject.getDescription() : "");
            
            // Date updates
            System.out.println("\nCurrent start date: " + CLIUtils.formatDate(existingProject.getStartDate()));
            String updateStartDate = CLIUtils.getInput(scanner, "Update start date? (y/N): ");
            LocalDate newStartDate = existingProject.getStartDate();
            if (updateStartDate.toLowerCase().startsWith("y")) {
                newStartDate = CLIUtils.getDateInput(scanner, "New start date", true);
            }
            
            System.out.println("\nCurrent end date: " + CLIUtils.formatDate(existingProject.getEndDate()));
            String updateEndDate = CLIUtils.getInput(scanner, "Update end date? (y/N): ");
            LocalDate newEndDate = existingProject.getEndDate();
            if (updateEndDate.toLowerCase().startsWith("y")) {
                newEndDate = CLIUtils.getDateInput(scanner, "New end date", true);
            }
            
            // Validate dates
            if (newStartDate != null && newEndDate != null && newEndDate.isBefore(newStartDate)) {
                CLIUtils.printError("End date cannot be before start date.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Apply updates
            existingProject.setName(newName);
            existingProject.setDescription(newDescription.isEmpty() ? null : newDescription);
            existingProject.setStartDate(newStartDate);
            existingProject.setEndDate(newEndDate);
            
            Project updatedProject = projectService.updateProject(existingProject);
            CLIUtils.printSuccess("Project updated successfully!");
            System.out.println("\nUpdated project:");
            System.out.println(CLIUtils.formatProject(updatedProject));
            
        } catch (ProjectNotFoundException e) {
            CLIUtils.printError("Project not found: " + e.getMessage());
        } catch (TodoManagementException e) {
            CLIUtils.printError("Failed to update project: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating project", e);
            CLIUtils.printError("An unexpected error occurred while updating the project.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Deletes a project after confirmation.
     */
    public void deleteProject() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Delete Project");
        
        try {
            Long projectId = (long) CLIUtils.getIntInput(scanner, "Enter project ID to delete: ", 1, Integer.MAX_VALUE);
            
            Project project = projectService.getProjectById(projectId);
            System.out.println("\nProject to be deleted:");
            System.out.println(CLIUtils.formatProject(project));
            
            // Check for assigned todos
            List<Todo> projectTodos = todoService.getTodosByProject(projectId);
            if (!projectTodos.isEmpty()) {
                System.out.println();
                CLIUtils.printWarning("This project has " + projectTodos.size() + " assigned todos!");
                System.out.println("Deleting the project will also remove todo assignments.");
            }
            
            System.out.println();
            String confirm = CLIUtils.getInput(scanner, "Are you sure you want to delete this project? (y/N): ");
            
            if (confirm.toLowerCase().startsWith("y")) {
                projectService.deleteProject(projectId);
                CLIUtils.printSuccess("Project deleted successfully!");
            } else {
                CLIUtils.printInfo("Project deletion cancelled.");
            }
            
        } catch (ProjectNotFoundException e) {
            CLIUtils.printError("Project not found: " + e.getMessage());
        } catch (TodoManagementException e) {
            CLIUtils.printError("Failed to delete project: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting project", e);
            CLIUtils.printError("An unexpected error occurred while deleting the project.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Views all todos assigned to a specific project.
     */
    public void viewProjectTodos() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Project Todos");
        
        try {
            Long projectId = (long) CLIUtils.getIntInput(scanner, "Enter project ID: ", 1, Integer.MAX_VALUE);
            
            Project project = projectService.getProjectById(projectId);
            List<Todo> todos = todoService.getTodosByProject(projectId);
            
            System.out.println("\nProject: " + project.getName());
            System.out.println("=".repeat(project.getName().length() + 9));
            
            if (todos.isEmpty()) {
                CLIUtils.printInfo("No todos assigned to this project.");
            } else {
                System.out.println("\nAssigned todos (" + todos.size() + "):");
                System.out.println();
                
                for (Todo todo : todos) {
                    System.out.println(CLIUtils.formatTodo(todo));
                    System.out.println();
                }
            }
            
        } catch (ProjectNotFoundException e) {
            CLIUtils.printError("Project not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error viewing project todos", e);
            CLIUtils.printError("An unexpected error occurred while retrieving project todos.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Assigns a todo to a project.
     */
    public void assignTodoToProject() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Assign Todo to Project");
        
        try {
            Long todoId = (long) CLIUtils.getIntInput(scanner, "Enter todo ID: ", 1, Integer.MAX_VALUE);
            Long projectId = (long) CLIUtils.getIntInput(scanner, "Enter project ID: ", 1, Integer.MAX_VALUE);
            
            // Verify both exist before assignment
            Todo todo = todoService.getTodoById(todoId);
            Project project = projectService.getProjectById(projectId);
            
            System.out.println("\nTodo: " + todo.getTitle());
            System.out.println("Project: " + project.getName());
            System.out.println();
            
            todoService.assignTodoToProject(todoId, projectId);
            CLIUtils.printSuccess("Todo assigned to project successfully!");
            
        } catch (TodoManagementException e) {
            CLIUtils.printError("Assignment failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error assigning todo to project", e);
            CLIUtils.printError("An unexpected error occurred while assigning the todo.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Views project progress and completion statistics.
     */
    public void viewProjectProgress() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Project Progress");
        
        try {
            Long projectId = (long) CLIUtils.getIntInput(scanner, "Enter project ID: ", 1, Integer.MAX_VALUE);
            
            Project project = projectService.getProjectById(projectId);
            ProjectService.ProjectCompletionStats stats = projectService.getProjectCompletionStats(projectId);
            
            System.out.println("\nProject: " + project.getName());
            System.out.println("=".repeat(project.getName().length() + 9));
            
            // Basic project info
            System.out.printf("Start Date: %s%n", CLIUtils.formatDate(project.getStartDate()));
            System.out.printf("End Date: %s%n", CLIUtils.formatDate(project.getEndDate()));
            
            // Progress statistics
            System.out.println("\nProgress Statistics:");
            System.out.printf("Total Todos: %s%d%s%n", CLIUtils.BOLD, stats.getTotalTodos(), CLIUtils.RESET);
            System.out.printf("  • %sCompleted:%s %d%n", CLIUtils.GREEN, CLIUtils.RESET, stats.getCompletedTodos());
            System.out.printf("  • %sIn Progress:%s %d%n", CLIUtils.BLUE, CLIUtils.RESET, stats.getInProgressTodos());
            System.out.printf("  • %sTodo:%s %d%n", CLIUtils.YELLOW, CLIUtils.RESET, stats.getTodoTodos());
            System.out.printf("  • %sCancelled:%s %d%n", CLIUtils.RED, CLIUtils.RESET, stats.getCancelledTodos());
            
            if (stats.getOverdueTodos() > 0) {
                System.out.printf("  • %sOverdue:%s %d%n", CLIUtils.RED + CLIUtils.BOLD, CLIUtils.RESET, stats.getOverdueTodos());
            }
            
            // Completion percentage
            System.out.printf("%nCompletion: %s%.1f%%%s%n", 
                CLIUtils.BOLD, stats.getCompletionPercentage(), CLIUtils.RESET);
            
            // Progress bar
            displayProgressBar(stats.getCompletionPercentage());
            
        } catch (ProjectNotFoundException e) {
            CLIUtils.printError("Project not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error viewing project progress", e);
            CLIUtils.printError("An unexpected error occurred while retrieving project progress.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Views detailed information about a specific project.
     */
    public void viewProjectDetails() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Project Details");
        
        try {
            Long projectId = (long) CLIUtils.getIntInput(scanner, "Enter project ID: ", 1, Integer.MAX_VALUE);
            
            Project project = projectService.getProjectById(projectId);
            List<Todo> todos = todoService.getTodosByProject(projectId);
            
            System.out.println();
            System.out.println("═══════════════════════════════════════");
            System.out.println("  PROJECT DETAILS");
            System.out.println("═══════════════════════════════════════");
            System.out.println();
            System.out.printf("ID: %s%d%s%n", CLIUtils.CYAN, project.getId(), CLIUtils.RESET);
            System.out.printf("Name: %s%s%s%n", CLIUtils.BOLD, project.getName(), CLIUtils.RESET);
            System.out.printf("Description: %s%n", project.getDescription() != null ? project.getDescription() : "None");
            System.out.printf("Start Date: %s%n", CLIUtils.formatDate(project.getStartDate()));
            System.out.printf("End Date: %s%n", CLIUtils.formatDate(project.getEndDate()));
            System.out.printf("Created: %s%n", CLIUtils.formatDateTime(project.getCreatedAt()));
            System.out.printf("Last Updated: %s%n", CLIUtils.formatDateTime(project.getUpdatedAt()));
            System.out.printf("Total Todos: %s%d%s%n", CLIUtils.BOLD, todos.size(), CLIUtils.RESET);
            
            // Show project status
            if (project.getEndDate() != null && project.getEndDate().isBefore(LocalDate.now())) {
                long completedTodos = todos.stream().mapToLong(todo -> todo.getStatus().isCompleted() ? 1 : 0).sum();
                if (completedTodos == todos.size() && !todos.isEmpty()) {
                    System.out.printf("Status: %sCOMPLETED%s%n", CLIUtils.GREEN + CLIUtils.BOLD, CLIUtils.RESET);
                } else {
                    System.out.printf("Status: %sOVERDUE%s%n", CLIUtils.RED + CLIUtils.BOLD, CLIUtils.RESET);
                }
            } else {
                System.out.printf("Status: %sACTIVE%s%n", CLIUtils.BLUE + CLIUtils.BOLD, CLIUtils.RESET);
            }
            
            // Show todo summary if todos exist
            if (!todos.isEmpty()) {
                System.out.println("\nTodo Summary:");
                long completed = todos.stream().mapToLong(todo -> todo.getStatus().isCompleted() ? 1 : 0).sum();
                long inProgress = todos.stream().mapToLong(todo -> todo.getStatus().isInProgress() ? 1 : 0).sum();
                long todoCount = todos.stream().mapToLong(t -> t.getStatus().isTodo() ? 1 : 0).sum();
                
                System.out.printf("  • %sCompleted:%s %d/%d%n", CLIUtils.GREEN, CLIUtils.RESET, completed, todos.size());
                System.out.printf("  • %sIn Progress:%s %d%n", CLIUtils.BLUE, CLIUtils.RESET, inProgress);
                System.out.printf("  • %sTodo:%s %d%n", CLIUtils.YELLOW, CLIUtils.RESET, todoCount);
            }
            
        } catch (ProjectNotFoundException e) {
            CLIUtils.printError("Project not found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error viewing project details", e);
            CLIUtils.printError("An unexpected error occurred while retrieving project details.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Displays project statistics.
     */
    public void displayProjectStatistics() {
        try {
            ProjectService.ProjectStatistics stats = projectService.getProjectStatistics();
            
            CLIUtils.printSectionHeader("Project Statistics");
            System.out.printf("Total Projects: %s%d%s%n", CLIUtils.BOLD, stats.getTotalProjects(), CLIUtils.RESET);
            System.out.printf("  • %sActive:%s %d%n", CLIUtils.BLUE, CLIUtils.RESET, stats.getActiveProjects());
            System.out.printf("  • %sCompleted:%s %d%n", CLIUtils.GREEN, CLIUtils.RESET, stats.getCompletedProjects());
            
        } catch (Exception e) {
            CLIUtils.printError("Error retrieving project statistics: " + e.getMessage());
        }
    }
    
    /**
     * Displays project completion statistics for all projects.
     */
    public void displayProjectCompletionStats() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Project Completion Statistics");
        
        try {
            List<Project> projects = projectService.getAllProjects();
            
            if (projects.isEmpty()) {
                CLIUtils.printInfo("No projects found.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            System.out.println();
            for (Project project : projects) {
                try {
                    ProjectService.ProjectCompletionStats stats = projectService.getProjectCompletionStats(project.getId());
                    
                    System.out.printf("%s[%d] %s%s%n", CLIUtils.CYAN, project.getId(), project.getName(), CLIUtils.RESET);
                    System.out.printf("  Todos: %d total, %d completed (%.1f%%)%n", 
                        stats.getTotalTodos(), stats.getCompletedTodos(), stats.getCompletionPercentage());
                    
                    // Mini progress bar
                    displayMiniProgressBar(stats.getCompletionPercentage());
                    System.out.println();
                    
                } catch (Exception e) {
                    System.out.printf("  Error retrieving stats for project %d%n", project.getId());
                }
            }
            
        } catch (Exception e) {
            CLIUtils.printError("Error retrieving project completion statistics: " + e.getMessage());
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Displays a progress bar for completion percentage.
     * 
     * @param percentage the completion percentage
     */
    private void displayProgressBar(double percentage) {
        int barLength = 40;
        int filledLength = (int) (percentage / 100.0 * barLength);
        
        System.out.print("\nProgress: [");
        System.out.print(CLIUtils.GREEN + "█".repeat(filledLength) + CLIUtils.RESET);
        System.out.print("░".repeat(barLength - filledLength));
        System.out.printf("] %.1f%%%n", percentage);
    }
    
    /**
     * Displays a mini progress bar for completion percentage.
     * 
     * @param percentage the completion percentage
     */
    private void displayMiniProgressBar(double percentage) {
        int barLength = 20;
        int filledLength = (int) (percentage / 100.0 * barLength);
        
        System.out.print("  [");
        System.out.print(CLIUtils.GREEN + "█".repeat(filledLength) + CLIUtils.RESET);
        System.out.print("░".repeat(barLength - filledLength));
        System.out.printf("] %.1f%%", percentage);
    }
}
