package org.lucian.todos.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Represents a recurring todo that repeats at specified intervals.
 * Extends the base Todo class with recurring functionality.
 */
public class RecurringTodo extends Todo {
    
    private Period recurringInterval;
    private LocalDate nextDueDate;
    private int maxOccurrences;
    private int currentOccurrence;
    
    /**
     * Default constructor for RecurringTodo.
     */
    public RecurringTodo() {
        super();
        this.currentOccurrence = 1;
        this.maxOccurrences = Integer.MAX_VALUE;
        // NOTE: this sets the max occurrences to (almost) infinite
    }
    
    /**
     * Constructor with title
     * 
     * @param title the todo title
     */
    public RecurringTodo(String title) {
        super(title);
        this.currentOccurrence = 1;
        this.maxOccurrences = Integer.MAX_VALUE;
    }
    
    /**
     * Full constructor
     * 
     * @param title the todo title
     * @param description the todo description
     * @param dueDate the first due date
     * @param priority the todo priority
     * @param recurringInterval the interval between occurrences
     */
    public RecurringTodo(String title, String description, LocalDate dueDate, 
                        Priority priority, Period recurringInterval) {
        super(title, description, dueDate, priority);
        this.recurringInterval = recurringInterval;
        this.currentOccurrence = 1;
        this.maxOccurrences = Integer.MAX_VALUE;
        calculateNextDueDate();
    }
    
    // get; set;
    
    public Period getRecurringInterval() {
        return recurringInterval;
    }
    
    public void setRecurringInterval(Period recurringInterval) {
        this.recurringInterval = recurringInterval;
        calculateNextDueDate();
    }
    
    public LocalDate getNextDueDate() {
        return nextDueDate;
    }
    
    public int getMaxOccurrences() {
        return maxOccurrences;
    }
    
    public void setMaxOccurrences(int maxOccurrences) {
        if (maxOccurrences < 1) {
            throw new IllegalArgumentException("Max occurrences must be at least 1");
        }
        this.maxOccurrences = maxOccurrences;
    }
    
    public int getCurrentOccurrence() {
        return currentOccurrence;
    }
    
    /**
     * Gets the recurrence frequency (alias for getCurrentOccurrence).
     * Used for CLI compatibility.
     * 
     * @return the current occurrence number
     */
    public int getRecurrenceFrequency() {
        return getCurrentOccurrence();
    }
    
    /**
     * Gets the recurrence interval (alias for getRecurringInterval).
     * Used for CLI compatibility.
     * 
     * @return the recurring interval
     */
    public Period getRecurrenceInterval() {
        return getRecurringInterval();
    }
    
    /**
     * Calculates the next due date based on current due date and interval.
     */
    private void calculateNextDueDate() {
        if (getDueDate() != null && recurringInterval != null) {
            this.nextDueDate = getDueDate().plus(recurringInterval);
        }
    }
    
    /**
     * Moves to the next occurrence of this recurring todo.
     * Updates the due date and occurrence counter.
     * 
     * @return true if successfully moved to next occurrence, false if max reached
     */
    public boolean moveToNextOccurrence() {
        if (currentOccurrence >= maxOccurrences) {
            return false;
        }
        
        if (recurringInterval != null && getDueDate() != null) {
            setDueDate(getDueDate().plus(recurringInterval));
            currentOccurrence++;
            calculateNextDueDate();
            setStatus(TodoStatus.TODO); // Reset status for new occurrence
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if this todo has more occurrences remaining.
     * 
     * @return true if more occurrences are possible
     */
    public boolean hasMoreOccurrences() {
        return currentOccurrence < maxOccurrences;
    }
    
    /**
     * Gets the remaining number of occurrences.
     * 
     * @return number of remaining occurrences, or -1 if infinite
     */
    public int getRemainingOccurrences() {
        if (maxOccurrences == Integer.MAX_VALUE) {
            return -1; // Infinite
        }
        return Math.max(0, maxOccurrences - currentOccurrence);
    }
    
    /**
     * Checks if this is the final occurrence.
     * 
     * @return true if this is the last occurrence
     */
    public boolean isFinalOccurrence() {
        return currentOccurrence >= maxOccurrences;
    }
    
    @Override
    public void markCompleted() {
        super.markCompleted();
        
        
        // If there are more occurrences, automatically create next one
        if (hasMoreOccurrences()) {
            moveToNextOccurrence();
        }
    }
    
    @Override
    public void validate() {
        super.validate();
        
        if (recurringInterval != null && recurringInterval.isZero()) {
            throw new IllegalStateException("Recurring interval cannot be zero");
        }
        
        if (maxOccurrences < 1) {
            throw new IllegalStateException("Max occurrences must be at least 1");
        }
        
        if (currentOccurrence < 1) {
            throw new IllegalStateException("Current occurrence must be at least 1");
        }
    }
    
    @Override
    public String toString() {
        String baseString = super.toString();
        return baseString.replace("Todo{", "RecurringTodo{") + 
               String.format(", occurrence=%d/%s, interval=%s", 
                           currentOccurrence, 
                           maxOccurrences == Integer.MAX_VALUE ? "∞" : maxOccurrences,
                           recurringInterval);
    }
}
