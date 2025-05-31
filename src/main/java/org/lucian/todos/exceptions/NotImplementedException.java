package org.lucian.todos.exceptions;

public class NotImplementedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "This feature is not implemented yet.";

    /**
     * Constructs a new NotImplementedException with the specified detail message.
     *
     * @param message the detail message
     */
    public NotImplementedException(String message) {
        super(message != null && !message.isEmpty() ? message : DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new NotImplementedException with no detail message.
     */
    public NotImplementedException() {
        super(DEFAULT_MESSAGE);
    }
}
