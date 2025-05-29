package org.lucian.todos.exceptions;

/**
 * Exception thrown when database operations fail.
 */
public class DatabaseException extends TodoManagementException {
    
    /**
     * Constructs a DatabaseException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a DatabaseException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getUserFriendlyMessage() {
        return "A database error occurred. Please try again later. " +
               "If the problem persists, contact support.";
    }
}