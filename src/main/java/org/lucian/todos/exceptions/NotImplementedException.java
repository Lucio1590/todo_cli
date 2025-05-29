package org.lucian.todos.exceptions;

public class NotImplementedException extends RuntimeException {

    /**
     * Constructs a new NotImplementedException with the specified detail message.
     *
     * @param message the detail message
     */
    public NotImplementedException(String message) {
        
        super(message != null && !message.isEmpty() ? message : "This feature is not implemented yet.");
        
    }
    
}
