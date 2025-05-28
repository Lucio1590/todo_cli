package org.lucian.todos.model;

/**
 * Enumeration defining different types of todos.
 * Used by the TodoFactory to create appropriate todo instances.
 * 
 * @author Lucian Diaconu
 * @since 1.0
 */

public enum TodoType {
    /**
     * Simple todo with basic properties
     */
    SIMPLE("Simple Todo"),
    
    /**
     * Recurring todo that repeats at intervals
     */
    RECURRING("Recurring Todo");

    private final String displayName;
    

    /**
     * Constructor for TodoType enum.
     *
     * @param displayName the name for this todo type
     */
    TodoType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
}
