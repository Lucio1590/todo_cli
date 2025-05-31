
package org.lucian.todos.exceptions;

/**
 * Base exception class for all task management related exceptions.
 * Build to control error propagation.
 */
public abstract class TodoManagementException extends Exception {
    
    /**
     * Constructs that accepts a detail message.
     * 
     * @param message the detail message
     */
    public TodoManagementException(String message) {
        super(message);
    }
    
    /**
     * Constructor that accepts a detail message and a cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public TodoManagementException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Gets a user-friendly error message that can be safely printed.
     * This method implements exception shielding by providing clean messages.
     * 
     * @return user-friendly error message
     */
    public abstract String getUserFriendlyMessage();
}
