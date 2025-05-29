package org.lucian.todos.model;

/**
* enum used to represent the priority of a task
*/
public enum Priority {
    /**
     * not urgent, can be done when time permits
     */
    LOW("Low"),

    /**
     *  normal priority level
     */
    MEDIUM("Medium"),

    /**
     * important and should be completed soon
     */
    HIGH("High"),

    /**
     *  must be completed immediately
     */
    URGENT("Urgent");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // TODO: add Priority from string

}
