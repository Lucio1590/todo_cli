package org.lucian.todos.model;

public enum TodoStatus {
    /**
     * Task has been created
     */
    TODO("To Do"),

    /**
     * Task is wip
     */
    IN_PROGRESS("In Progress"),

    /**
     * Task has been completed
     */
    COMPLETED("Completed"),

    /**
     * Task has been canceled
     */
    CANCELLED("Cancelled");

    private final String displayName;

    /**
     * Constructor for TaskStatus enum.
     *
     * @param displayName the human-readable name for this status
     */
    TodoStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Gets the display name in a string type
     *
     * @return the string status name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the task is not in progress anymore
     *
     * @return true if the task is completed or cancelled
     */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * Checks if this status allows task modification.
     *
     * @return true if the task can be modified
     */
    public boolean isModifiable() {
        return this == TODO || this == IN_PROGRESS;
    }

    /**
     * Checks if this status represents a completed task.
     *
     * @return true if the task is completed
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Checks if this status represents a todo task.
     *
     * @return true if the task is in todo state
     */
    public boolean isTodo() {
        return this == TODO;
    }

    /**
     * Checks if this status represents a task in progress.
     *
     * @return true if the task is in progress
     */
    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }

}
