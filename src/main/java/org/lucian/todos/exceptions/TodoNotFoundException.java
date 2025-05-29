package org.lucian.todos.exceptions;

/**
 * Exception thrown when a todo is not found.
 * 
 * @author Lucian Diaconu
 * @since 1.0
 */
public class TodoNotFoundException extends TodoManagementException {
    
    private final Long todoId;
    
    /**
     * Constructs a TaskNotFoundException with the specified todo ID.
     * 
     * @param todoId the ID of the todo that was not found
     */
    public TodoNotFoundException(Long todoId) {
        super("Task not found: " + todoId);
        this.todoId = todoId;
    }
    
    /**
     * Constructs a TaskNotFoundException with the specified message and todo ID.
     * 
     * @param message the detail message
     * @param todoId the ID of the todo that was not found
     */
    public TodoNotFoundException(String message, Long todoId) {
        super(message);
        this.todoId = todoId;
    }
    
    /**
     * Constructs a TaskNotFoundException with the specified message, todo ID, and cause.
     * 
     * @param message the detail message
     * @param todoId the ID of the todo that was not found
     * @param cause the cause of this exception
     */
    public TodoNotFoundException(String message, Long todoId, Throwable cause) {
        super(message, cause);
        this.todoId = todoId;
    }
    
    /**
     * Gets the todo ID that was not found.
     * 
     * @return the todo ID
     */
    public Long getTaskId() {
        return todoId;
    }
    
    @Override
    public String getUserFriendlyMessage() {
        return todoId != null 
            ? "The requested todo (ID: " + todoId + ") could not be found."
            : "The requested todo could not be found.";
    }
}
