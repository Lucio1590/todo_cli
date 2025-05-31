package org.lucian.todos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.dao.ProjectDAO;
import org.lucian.todos.dao.TodoDAO;
import org.lucian.todos.exceptions.AuthenticationException;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.exceptions.ProjectNotFoundException;
import org.lucian.todos.model.Project;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;
import org.lucian.todos.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for project business logic operations.
 * Handles project management, validation, and business rule enforcement.
 * 

 */
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    
    private final ProjectDAO projectDAO;
    private final TodoDAO todoDAO;
    private final AuthenticationService authService;
    
    public ProjectService(ProjectDAO projectDAO, TodoDAO todoDAO, AuthenticationService authService) {
        this.projectDAO = projectDAO;
        this.todoDAO = todoDAO;
        this.authService = authService;
    }
    
    /**
     * Creates a new project with validation.
     * 
     * @param project the project to create
     * @return the created project with generated ID
     * @throws DatabaseException if creation fails
     * @throws IllegalArgumentException if project data is invalid
     */
    public Project createProject(Project project) throws DatabaseException {
        validateProject(project);
        
        // Set the user_id to the current authenticated user
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                throw new AuthenticationException("No user is currently authenticated");
            }
            project.setUserId(currentUser.getId());
        } catch (AuthenticationException e) {
            logger.error("Failed to get current user for project creation", e);
            throw new DatabaseException("Cannot create project: user not authenticated", e);
        }
        
        logger.info("Creating new project: {} for user: {}", project.getName(), project.getUserId());
        
        return projectDAO.create(project);
    }
    
    /**
     * Finds a project by ID and loads its todos.
     * 
     * @param id the project ID
     * @return the project with loaded todos
     * @throws ProjectNotFoundException if project is not found
     * @throws DatabaseException if query fails
     */
    public Project findProjectById(Long id) throws ProjectNotFoundException, DatabaseException {
        if (id == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        
        logger.debug("Finding project by ID: {}", id);
        
        Optional<Project> project = projectDAO.findById(id);
        if (project.isEmpty()) {
            throw new ProjectNotFoundException("Project not found with ID: " + id, id);
        }
        
        // Load todos for the project
        Project foundProject = project.get();
        List<Todo> todos = todoDAO.findByProjectId(id);
        
        // Clear existing todos and add loaded ones
        foundProject.getTodos().clear();
        todos.forEach(foundProject::addTodo);
        
        return foundProject;
    }
    
    /**
     * Retrieves all projects.
     * 
     * @return list of all projects
     * @throws DatabaseException if query fails
     */
    public List<Project> getAllProjects() throws DatabaseException {
        logger.debug("Retrieving all projects");
        return projectDAO.findAll();
    }
    
    /**
     * Finds projects by name (partial match).
     * 
     * @param name the project name to search for
     * @return list of matching projects
     * @throws DatabaseException if query fails
     */
    public List<Project> findProjectsByName(String name) throws DatabaseException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        
        logger.debug("Finding projects by name: {}", name);
        return projectDAO.findByName(name);
    }
    
    /**
     * Gets all completed projects.
     * 
     * @return list of completed projects
     * @throws DatabaseException if query fails
     */
    public List<Project> getCompletedProjects() throws DatabaseException {
        logger.debug("Finding completed projects");
        return projectDAO.findCompleted();
    }
    
    /**
     * Gets all active projects (with incomplete todos).
     * 
     * @return list of active projects
     * @throws DatabaseException if query fails
     */
    public List<Project> getActiveProjects() throws DatabaseException {
        logger.debug("Finding active projects");
        return projectDAO.findActive();
    }
    
    /**
     * Updates an existing project with validation.
     * 
     * @param project the project to update
     * @return the updated project
     * @throws ProjectNotFoundException if project is not found
     * @throws DatabaseException if update fails
     * @throws IllegalArgumentException if project data is invalid
     */
    public Project updateProject(Project project) throws ProjectNotFoundException, DatabaseException {
        validateProject(project);
        
        if (project.getId() == null) {
            throw new IllegalArgumentException("Project ID cannot be null for update");
        }
        
        // Verify project exists
        if (!projectDAO.exists(project.getId())) {
            throw new ProjectNotFoundException("Project not found with ID: " + project.getId(), project.getId());
        }
        
        logger.info("Updating project: {} (ID: {})", project.getName(), project.getId());
        return projectDAO.update(project);
    }
    
    /**
     * Adds a todo to a project.
     * 
     * @param projectId the project ID
     * @param todo the todo to add
     * @return the updated todo with project assignment
     * @throws ProjectNotFoundException if project is not found
     * @throws DatabaseException if operation fails
     */
    public Todo addTodoToProject(Long projectId, Todo todo) 
            throws ProjectNotFoundException, DatabaseException {
        if (!projectDAO.exists(projectId)) {
            throw new ProjectNotFoundException("Project not found with ID: " + projectId, projectId);
        }
        
        logger.info("Adding todo '{}' to project {}", todo.getTitle(), projectId);
        
        todo.setProjectId(projectId);
        
        if (todo.getId() == null) {
            // Create new todo
            return todoDAO.create(todo);
        } else {
            // Update existing todo
            return todoDAO.update(todo);
        }
    }
    
    /**
     * Removes a todo from a project.
     * 
     * @param projectId the project ID
     * @param todoId the todo ID
     * @return the updated todo
     * @throws ProjectNotFoundException if project is not found
     * @throws DatabaseException if operation fails
     */
    public Todo removeTodoFromProject(Long projectId, Long todoId) 
            throws ProjectNotFoundException, DatabaseException {
        if (!projectDAO.exists(projectId)) {
            throw new ProjectNotFoundException("Project not found with ID: " + projectId, projectId);
        }
        
        Optional<Todo> todoOpt = todoDAO.findById(todoId);
        if (todoOpt.isEmpty()) {
            throw new IllegalArgumentException("Todo not found with ID: " + todoId);
        }
        
        Todo todo = todoOpt.get();
        if (!projectId.equals(todo.getProjectId())) {
            throw new IllegalArgumentException("Todo " + todoId + " is not assigned to project " + projectId);
        }
        
        logger.info("Removing todo {} from project {}", todoId, projectId);
        
        todo.setProjectId(null);
        return todoDAO.update(todo);
    }
    
    /**
     * Gets project completion statistics.
     * 
     * @param projectId the project ID
     * @return completion statistics
     * @throws ProjectNotFoundException if project is not found
     * @throws DatabaseException if query fails
     */
    public ProjectCompletionStats getProjectCompletionStats(Long projectId) 
            throws ProjectNotFoundException, DatabaseException {
        if (!projectDAO.exists(projectId)) {
            throw new ProjectNotFoundException("Project not found with ID: " + projectId, projectId);
        }
        
        logger.debug("Calculating completion stats for project: {}", projectId);
        
        List<Todo> todos = todoDAO.findByProjectId(projectId);
        
        ProjectCompletionStats stats = new ProjectCompletionStats();
        stats.setProjectId(projectId);
        stats.setTotalTodos(todos.size());
        
        if (todos.isEmpty()) {
            return stats;
        }
        
        long completedTodos = todos.stream()
                                   .mapToLong(todo -> todo.getStatus() == TodoStatus.COMPLETED ? 1 : 0)
                                   .sum();
        
        long todoTodos = todos.stream()
                              .mapToLong(todo -> todo.getStatus() == TodoStatus.TODO ? 1 : 0)
                              .sum();
        
        long inProgressTodos = todos.stream()
                                    .mapToLong(todo -> todo.getStatus() == TodoStatus.IN_PROGRESS ? 1 : 0)
                                    .sum();
        
        long cancelledTodos = todos.stream()
                                   .mapToLong(todo -> todo.getStatus() == TodoStatus.CANCELLED ? 1 : 0)
                                   .sum();
        
        long overdueTodos = todos.stream()
                                 .mapToLong(todo -> todo.isOverdue() ? 1 : 0)
                                 .sum();
        
        stats.setCompletedTodos(completedTodos);
        stats.setTodoTodos(todoTodos);
        stats.setInProgressTodos(inProgressTodos);
        stats.setCancelledTodos(cancelledTodos);
        stats.setOverdueTodos(overdueTodos);
        stats.setCompletionPercentage((completedTodos * 100.0) / todos.size());
        
        return stats;
    }
    
    /**
     * Deletes a project and all its todos.
     * 
     * @param projectId the project ID
     * @return true if project was deleted, false if not found
     * @throws DatabaseException if deletion fails
     */
    public boolean deleteProject(Long projectId) throws DatabaseException {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        
        logger.info("Deleting project: {}", projectId);
        return projectDAO.delete(projectId);
    }
    
    /**
     * Gets overall project statistics.
     * 
     * @return project statistics
     * @throws DatabaseException if query fails
     */
    public ProjectStatistics getProjectStatistics() throws DatabaseException {
        logger.debug("Calculating project statistics");
        
        ProjectStatistics stats = new ProjectStatistics();
        stats.setTotalProjects(projectDAO.count());
        stats.setActiveProjects(projectDAO.findActive().size());
        stats.setCompletedProjects(projectDAO.findCompleted().size());
        
        return stats;
    }
    
    /**
     * Gets a project by ID (alias for findProjectById for CLI compatibility).
     * 
     * @param id the project ID
     * @return the project with loaded todos
     * @throws ProjectNotFoundException if project is not found
     * @throws DatabaseException if query fails
     */
    public Project getProjectById(Long id) throws ProjectNotFoundException, DatabaseException {
        return findProjectById(id);
    }
    
    /**
     * Validates project data.
     * 
     * @param project the project to validate
     * @throws IllegalArgumentException if project data is invalid
     */
    private void validateProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        
        if (project.getName().length() > 255) {
            throw new IllegalArgumentException("Project name cannot exceed 255 characters");
        }
        
        if (project.getDescription() != null && project.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Project description cannot exceed 1000 characters");
        }
        
        // Business rule: End date should be after start date
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getEndDate().isBefore(project.getStartDate())) {
                throw new IllegalArgumentException("Project end date cannot be before start date");
            }
        }
        
        // Business rule: Warn if project is created with end date in the past
        if (project.getId() == null && project.getEndDate() != null 
            && project.getEndDate().isBefore(LocalDate.now())) {
            logger.warn("Project created with end date in the past: {}", project.getName());
        }
    }
    
    /**
     * Inner class for project completion statistics.
     */
    public static class ProjectCompletionStats {
        private Long projectId;
        private int totalTodos;
        private long completedTodos;
        private long todoTodos;
        private long inProgressTodos;
        private long cancelledTodos;
        private long overdueTodos;
        private double completionPercentage;
        
        // Getters and setters
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }
        
        public int getTotalTodos() { return totalTodos; }
        public void setTotalTodos(int totalTodos) { this.totalTodos = totalTodos; }
        
        public long getCompletedTodos() { return completedTodos; }
        public void setCompletedTodos(long completedTodos) { this.completedTodos = completedTodos; }
        
        public long getTodoTodos() { return todoTodos; }
        public void setTodoTodos(long todoTodos) { this.todoTodos = todoTodos; }
        
        public long getInProgressTodos() { return inProgressTodos; }
        public void setInProgressTodos(long inProgressTodos) { this.inProgressTodos = inProgressTodos; }
        
        public long getCancelledTodos() { return cancelledTodos; }
        public void setCancelledTodos(long cancelledTodos) { this.cancelledTodos = cancelledTodos; }
        
        public long getOverdueTodos() { return overdueTodos; }
        public void setOverdueTodos(long overdueTodos) { this.overdueTodos = overdueTodos; }
        
        public double getCompletionPercentage() { return completionPercentage; }
        public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }
        
        @Override
        public String toString() {
            return String.format(
                "ProjectCompletionStats{projectId=%d, total=%d, completed=%d, completion=%.1f%%}",
                projectId, totalTodos, completedTodos, completionPercentage
            );
        }
    }
    
    /**
     * Inner class for overall project statistics.
     */
    public static class ProjectStatistics {
        private long totalProjects;
        private long activeProjects;
        private long completedProjects;
        
        // Getters and setters
        public long getTotalProjects() { return totalProjects; }
        public void setTotalProjects(long totalProjects) { this.totalProjects = totalProjects; }
        
        public long getActiveProjects() { return activeProjects; }
        public void setActiveProjects(long activeProjects) { this.activeProjects = activeProjects; }
        
        public long getCompletedProjects() { return completedProjects; }
        public void setCompletedProjects(long completedProjects) { this.completedProjects = completedProjects; }
        
        @Override
        public String toString() {
            return String.format(
                "ProjectStatistics{total=%d, active=%d, completed=%d}",
                totalProjects, activeProjects, completedProjects
            );
        }
    }
}
