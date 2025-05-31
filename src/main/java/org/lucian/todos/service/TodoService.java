package org.lucian.todos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.dao.TodoDAO;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.exceptions.TodoNotFoundException;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.RecurringTodo;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;
import org.lucian.todos.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for todo business logic operations.
 * Handles todo management, validation, and business rule enforcement.
 */
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
    
    private final TodoDAO todoDAO;
    private final AuthenticationService authService;
    
    public TodoService(TodoDAO todoDAO, AuthenticationService authService) {
        this.todoDAO = todoDAO;
        this.authService = authService;
    }
    
    /**
     * Creates a new todo with validation.
     * 
     * @param todo the todo to create
     * @return the created todo with generated ID
     * @throws DatabaseException if creation fails
     * @throws IllegalArgumentException if todo data is invalid
     */
    public Todo createTodo(Todo todo) throws DatabaseException {
        try {
            validateTodo(todo);
            
            logger.info("Creating new todo: {}", todo.getTitle());
            
            // Ensure proper defaults
            if (todo.getPriority() == null) {
                todo.setPriority(Priority.MEDIUM);
            }
            if (todo.getStatus() == null) {
                todo.setStatus(TodoStatus.TODO);
            }
            
            // Set the current user ID to the todo
            User currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                todo.setUserId(currentUser.getId());
                logger.debug("Setting user ID {} for todo {}", currentUser.getId(), todo.getTitle());
            } else {
                logger.error("No user is currently authenticated");
                throw new IllegalStateException("No user is currently authenticated. Cannot create todo.");
            }
            
            return todoDAO.create(todo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Rethrow application-level exceptions with clear messages
            logger.warn("Error creating todo: {}", e.getMessage());
            throw e;
        } catch (DatabaseException e) {
            // Wrap database exceptions with user-friendly message
            logger.error("Database error while creating todo", e);
            throw new DatabaseException("Failed to create todo. Please try again or contact support.", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            logger.error("Unexpected error creating todo", e);
            throw new DatabaseException("An unexpected error occurred. Please try again or contact support.", e);
        }
    }
    
    /**
     * Finds a todo by ID.
     * 
     * @param id the todo ID
     * @return the todo if found
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if query fails
     */
    public Todo findTodoById(Long id) throws TodoNotFoundException, DatabaseException {
        if (id == null) {
            throw new IllegalArgumentException("Todo ID cannot be null");
        }
        
        logger.debug("Finding todo by ID: {}", id);
        
        Optional<Todo> todo = todoDAO.findById(id);
        if (todo.isEmpty()) {
            throw new TodoNotFoundException("Todo not found with ID: " + id, id);
        }
        
        return todo.get();
    }
    
    /**
     * Retrieves all todos.
     * 
     * @return list of all todos
     * @throws DatabaseException if query fails
     */
    public List<Todo> getAllTodos() throws DatabaseException {
        logger.debug("Retrieving all todos");
        return todoDAO.findAll();
    }
    
    /**
     * Finds todos by project ID.
     * 
     * @param projectId the project ID
     * @return list of todos in the project
     * @throws DatabaseException if query fails
     */
    public List<Todo> getTodosByProject(Long projectId) throws DatabaseException {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        
        logger.debug("Finding todos for project: {}", projectId);
        return todoDAO.findByProjectId(projectId);
    }
    
    /**
     * Finds todos by status.
     * 
     * @param status the todo status
     * @return list of todos with the specified status
     * @throws DatabaseException if query fails
     */
    public List<Todo> getTodosByStatus(TodoStatus status) throws DatabaseException {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        logger.debug("Finding todos by status: {}", status);
        return todoDAO.findByStatus(status);
    }
    
    /**
     * Finds todos by priority.
     * 
     * @param priority the todo priority
     * @return list of todos with the specified priority
     * @throws DatabaseException if query fails
     */
    public List<Todo> getTodosByPriority(Priority priority) throws DatabaseException {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        
        logger.debug("Finding todos by priority: {}", priority);
        return todoDAO.findByPriority(priority);
    }
    
    /**
     * Gets all overdue todos.
     * 
     * @return list of overdue todos
     * @throws DatabaseException if query fails
     */
    public List<Todo> getOverdueTodos() throws DatabaseException {
        logger.debug("Finding overdue todos");
        return todoDAO.findOverdue();
    }
    
    /**
     * Gets todos due today.
     * 
     * @return list of todos due today
     * @throws DatabaseException if query fails
     */
    public List<Todo> getTodosDueToday() throws DatabaseException {
        logger.debug("Finding todos due today");
        return todoDAO.findDueBefore(LocalDate.now().plusDays(1))
                      .stream()
                      .filter(todo -> todo.getDueDate() != null && 
                                    todo.getDueDate().equals(LocalDate.now()))
                      .toList();
    }
    
    /**
     * Updates an existing todo with validation.
     * 
     * @param todo the todo to update
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     * @throws IllegalArgumentException if todo data is invalid
     */
    public Todo updateTodo(Todo todo) throws TodoNotFoundException, DatabaseException {
        validateTodo(todo);
        
        if (todo.getId() == null) {
            throw new IllegalArgumentException("Todo ID cannot be null for update");
        }
        
        // Verify todo exists
        findTodoById(todo.getId());
        
        logger.info("Updating todo: {} (ID: {})", todo.getTitle(), todo.getId());
        return todoDAO.update(todo);
    }
    
    /**
     * Marks a todo as completed.
     * 
     * @param todoId the todo ID
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     */
    public Todo markTodoCompleted(Long todoId) throws TodoNotFoundException, DatabaseException {
        Todo todo = findTodoById(todoId);
        
        if (todo.getStatus() == TodoStatus.COMPLETED) {
            logger.info("Todo {} is already completed", todoId);
            return todo;
        }
        
        logger.info("Marking todo as completed: {} (ID: {})", todo.getTitle(), todoId);
        
        todo.markCompleted();
        
        // Handle recurring todo progression
        if (todo instanceof RecurringTodo recurringTodo && recurringTodo.hasMoreOccurrences()) {
            logger.info("Moving recurring todo to next occurrence: {}", todoId);
            recurringTodo.moveToNextOccurrence();
        }
        
        return todoDAO.update(todo);
    }
    
    /**
     * Marks a todo as in progress.
     * 
     * @param todoId the todo ID
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     */
    public Todo markTodoInProgress(Long todoId) throws TodoNotFoundException, DatabaseException {
        Todo todo = findTodoById(todoId);
        
        if (!todo.isModifiable()) {
            throw new IllegalStateException("Cannot modify todo in status: " + todo.getStatus());
        }
        
        logger.info("Marking todo as in progress: {} (ID: {})", todo.getTitle(), todoId);
        
        todo.markInProgress();
        return todoDAO.update(todo);
    }
    
    /**
     * Marks a todo as cancelled.
     * 
     * @param todoId the todo ID
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     */
    public Todo markTodoCancelled(Long todoId) throws TodoNotFoundException, DatabaseException {
        Todo todo = findTodoById(todoId);
        
        if (!todo.isModifiable()) {
            throw new IllegalStateException("Cannot modify todo in status: " + todo.getStatus());
        }
        
        logger.info("Marking todo as cancelled: {} (ID: {})", todo.getTitle(), todoId);
        
        todo.markCancelled();
        return todoDAO.update(todo);
    }
    
    /**
     * Assigns a todo to a project.
     * 
     * @param todoId the todo ID
     * @param projectId the project ID
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     */
    public Todo assignTodoToProject(Long todoId, Long projectId) 
            throws TodoNotFoundException, DatabaseException {
        Todo todo = findTodoById(todoId);
        
        logger.info("Assigning todo {} to project {}", todoId, projectId);
        
        todo.setProjectId(projectId);
        return todoDAO.update(todo);
    }
    
    /**
     * Removes a todo from its project.
     * 
     * @param todoId the todo ID
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     */
    public Todo removeTodoFromProject(Long todoId) throws TodoNotFoundException, DatabaseException {
        Todo todo = findTodoById(todoId);
        
        logger.info("Removing todo {} from project", todoId);
        
        todo.setProjectId(null);
        return todoDAO.update(todo);
    }
    
    /**
     * Deletes a todo.
     * 
     * @param todoId the todo ID
     * @return true if todo was deleted, false if not found
     * @throws DatabaseException if deletion fails
     */
    public boolean deleteTodo(Long todoId) throws DatabaseException {
        if (todoId == null) {
            throw new IllegalArgumentException("Todo ID cannot be null");
        }
        
        logger.info("Deleting todo: {}", todoId);
        return todoDAO.delete(todoId);
    }
    
    /**
     * Gets todo statistics.
     * 
     * @return todo statistics
     * @throws DatabaseException if query fails
     */
    public TodoStatistics getTodoStatistics() throws DatabaseException {
        logger.debug("Calculating todo statistics");
        
        TodoStatistics stats = new TodoStatistics();
        stats.setTotalTodos(todoDAO.count());
        stats.setTodoTodos(todoDAO.countByStatus(TodoStatus.TODO));
        stats.setInProgressTodos(todoDAO.countByStatus(TodoStatus.IN_PROGRESS));
        stats.setCompletedTodos(todoDAO.countByStatus(TodoStatus.COMPLETED));
        stats.setCancelledTodos(todoDAO.countByStatus(TodoStatus.CANCELLED));
        stats.setOverdueTodos(todoDAO.findOverdue().size());
        
        return stats;
    }
    
    /**
     * Gets a todo by ID (alias for findTodoById for CLI compatibility).
     * 
     * @param id the todo ID
     * @return the todo if found
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if query fails
     */
    public Todo getTodoById(Long id) throws TodoNotFoundException, DatabaseException {
        return findTodoById(id);
    }
    
    /**
     * Updates the status of a todo.
     * 
     * @param todoId the todo ID
     * @param newStatus the new status
     * @return the updated todo
     * @throws TodoNotFoundException if todo is not found
     * @throws DatabaseException if update fails
     */
    public Todo updateTodoStatus(Long todoId, TodoStatus newStatus) throws TodoNotFoundException, DatabaseException {
        Todo todo = findTodoById(todoId);
        
        if (!todo.isModifiable() && newStatus != TodoStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify todo in status: " + todo.getStatus());
        }
        
        logger.info("Updating todo status: {} (ID: {}) to {}", todo.getTitle(), todoId, newStatus);
        
        todo.setStatus(newStatus);
        return todoDAO.update(todo);
    }
    
    /**
     * Searches for todos by title or description containing the search term.
     * 
     * @param searchTerm the search term
     * @return list of matching todos
     * @throws DatabaseException if query fails
     */
    public List<Todo> searchTodos(String searchTerm) throws DatabaseException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        
        logger.debug("Searching todos with term: {}", searchTerm);
        
        // Get all todos and filter in memory for simplicity
        // In a real application, this should be done at the database level
        return todoDAO.findAll().stream()
                .filter(todo -> 
                    (todo.getTitle() != null && todo.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) ||
                    (todo.getDescription() != null && todo.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                )
                .toList();
    }
    
    /**
     * Validates todo data.
     * 
     * @param todo the todo to validate
     * @throws IllegalArgumentException if todo data is invalid
     */
    private void validateTodo(Todo todo) {
        if (todo == null) {
            throw new IllegalArgumentException("Todo cannot be null");
        }
        
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be null or empty");
        }
        
        if (todo.getTitle().length() > 255) {
            throw new IllegalArgumentException("Todo title cannot exceed 255 characters");
        }
        
        if (todo.getDescription() != null && todo.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Todo description cannot exceed 1000 characters");
        }
        
        // Validate recurring todo specific rules
        if (todo instanceof RecurringTodo recurringTodo) {
            if (recurringTodo.getRecurringInterval() == null) {
                throw new IllegalArgumentException("Recurring todo must have a recurring interval");
            }
            
            if (recurringTodo.getMaxOccurrences() < 1) {
                throw new IllegalArgumentException("Max occurrences must be at least 1");
            }
            
            if (recurringTodo.getCurrentOccurrence() < 1) {
                throw new IllegalArgumentException("Current occurrence must be at least 1");
            }
        }
        
        // Business rule: High and Urgent priority todos should have due dates
        if ((todo.getPriority() == Priority.HIGH || todo.getPriority() == Priority.URGENT) 
            && todo.getDueDate() == null) {
            logger.warn("High/Urgent priority todo created without due date: {}", todo.getTitle());
        }
        
        // Business rule: Warn if due date is in the past for new todos
        if (todo.getId() == null && todo.getDueDate() != null 
            && todo.getDueDate().isBefore(LocalDate.now())) {
            logger.warn("Todo created with due date in the past: {}", todo.getTitle());
        }
    }
    
    /**
     * Inner class for todo statistics.
     */
    public static class TodoStatistics {
        private long totalTodos;
        private long todoTodos;
        private long inProgressTodos;
        private long completedTodos;
        private long cancelledTodos;
        private long overdueTodos;
        
        // Getters and setters
        public long getTotalTodos() { return totalTodos; }
        public void setTotalTodos(long totalTodos) { this.totalTodos = totalTodos; }
        
        public long getTodoTodos() { return todoTodos; }
        public void setTodoTodos(long todoTodos) { this.todoTodos = todoTodos; }
        
        public long getInProgressTodos() { return inProgressTodos; }
        public void setInProgressTodos(long inProgressTodos) { this.inProgressTodos = inProgressTodos; }
        
        public long getCompletedTodos() { return completedTodos; }
        public void setCompletedTodos(long completedTodos) { this.completedTodos = completedTodos; }
        
        public long getCancelledTodos() { return cancelledTodos; }
        public void setCancelledTodos(long cancelledTodos) { this.cancelledTodos = cancelledTodos; }
        
        public long getOverdueTodos() { return overdueTodos; }
        public void setOverdueTodos(long overdueTodos) { this.overdueTodos = overdueTodos; }
        
        // Priority-based todo counts for CLI compatibility
        public long getUrgentTodos() {
            // This would typically be calculated by counting todos with URGENT priority
            // For now, we'll return 0 as this requires additional DAO methods
            return 0;
        }
        
        public long getHighPriorityTodos() {
            // This would typically be calculated by counting todos with HIGH priority
            return 0;
        }
        
        public long getMediumPriorityTodos() {
            // This would typically be calculated by counting todos with MEDIUM priority
            return 0;
        }
        
        public long getLowPriorityTodos() {
            // This would typically be calculated by counting todos with LOW priority
            return 0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "TodoStatistics{total=%d, todo=%d, inProgress=%d, completed=%d, cancelled=%d, overdue=%d}",
                totalTodos, todoTodos, inProgressTodos, completedTodos, cancelledTodos, overdueTodos
            );
        }
    }
}
