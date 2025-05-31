package org.lucian.todos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/* 
* Represents a project that contains multiple todos.
* Implements the Composite pattern to group todos together.
*/
public class Project {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private final List<Todo> todos;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
    * Default constructor for Project.
    */
    public Project() {
        this.todos = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Constructor with name for Project.
     * 
     * @param name the project name, must not be null or empty
     * @throws IllegalArgumentException if name is null or empty
     */
    public Project(String name) {
        this();
        setName(name);
    }

    /**
     * Full Project constructor.
     * 
     * @param name the project name
     * @param description the project description
     * @param startDate the project start date
     * @param endDate the project end date
     */
    public Project(String name, String description, LocalDate startDate, LocalDate endDate) {
        this(name);
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // get; set;
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        this.name = name.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    

    /**
     * Removes a todo by ID from this project.
     * 
     * @param todoId the ID of the todo to remove
     * @return true if the todo was removed, false if not found
     */
    public boolean removeTodoById(Long todoId) {
        if (todoId == null) {
            return false;
        }
        return todos.removeIf(todo -> Objects.equals(todo.getId(), todoId));
    }
    
    /**
     * Gets all todos in this project.
     * 
     * @return an unmodifiable list of todos
     */
    public List<Todo> getTodos() {
        return Collections.unmodifiableList(todos);
    }
    
    /**
     * Gets the number of todos in this project.
     * 
     * @return the todo count
     */
    public int getTodoCount() {
        return todos.size();
    }
    
    /**
     * Finds a todo by ID within this project.
     * 
     * @param todoId the todo ID to search for
     * @return the todo if found, null otherwise
     */
    public Todo findTodoById(Long todoId) {
        if (todoId == null) {
            return null;
        }
        return todos.stream()
                   .filter(todo -> Objects.equals(todo.getId(), todoId))
                   .findFirst()
                   .orElse(null);
    }
    
    /**
     * Gets todos by status.
     * 
     * @param status the status to filter by
     * @return list of todos with the specified status
     */
    public List<Todo> getTodosByStatus(TodoStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        return todos.stream()
                   .filter(todo -> todo.getStatus() == status)
                   .toList();
    }
    
    /**
     * Gets todos by priority.
     * 
     * @param priority the priority to filter by
     * @return list of todos with the specified priority
     */
    public List<Todo> getTodosByPriority(Priority priority) {
        if (priority == null) {
            return new ArrayList<>();
        }
        return todos.stream()
                   .filter(todo -> todo.getPriority() == priority)
                   .toList();
    }
    
    /**
     * Gets overdue todos in this project.
     * 
     * @return list of overdue todos
     */
    public List<Todo> getOverdueTodos() {
        return todos.stream()
                   .filter(Todo::isOverdue)
                   .toList();
    }
    
    /**
     * Gets completed todos in this project.
     * 
     * @return list of completed todos
     */
    public List<Todo> getCompletedTodos() {
        return getTodosByStatus(TodoStatus.COMPLETED);
    }
    
    /**
     * Calculates the completion percentage of this project.
     * 
     * @return completion percentage (0.0 to 100.0)
     */
    public double getCompletionPercentage() {
        if (todos.isEmpty()) {
            return 0.0;
        }
        long completedCount = todos.stream()
                                  .mapToLong(todo -> todo.getStatus() == TodoStatus.COMPLETED ? 1 : 0)
                                  .sum();
        return (completedCount * 100.0) / todos.size();
    }
    
    /**
     * Checks if the project is completed (all todos completed).
     * 
     * @return true if all todos are completed
     */
    public boolean isCompleted() {
        return !todos.isEmpty() && 
               todos.stream().allMatch(todo -> todo.getStatus() == TodoStatus.COMPLETED);
    }
    
    /**
     * Checks if the project is overdue.
     * 
     * @return true if the project end date is in the past and not completed
     */
    public boolean isOverdue() {
        return endDate != null && 
               LocalDate.now().isAfter(endDate) && 
               !isCompleted();
    }
    
    /**
     * Validates the project data.
     * 
     * @throws IllegalStateException if the project is in an invalid state
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Project must have a name");
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalStateException("Project start date cannot be after end date");
        }
    }
    
    /**
     * Creates an iterator for the todos in this project.
     * Implements the Iterator pattern.
     * 
     * @return iterator over the project's todos
     */
    public Iterator<Todo> iterator() {
        return new ProjectTodoIterator();
    }
    
    /**
     * Inner class implementing Iterator pattern for project todos.
     */
    private class ProjectTodoIterator implements Iterator<Todo> {
        private int currentIndex = 0;
        
        @Override
        public boolean hasNext() {
            return currentIndex < todos.size();
        }
        
        @Override
        public Todo next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more todos in this project");
            }
            return todos.get(currentIndex++);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) && 
               Objects.equals(name, project.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return String.format("Project{id=%d, name='%s', todos=%d, completion=%.1f%%}", 
                           id, name, todos.size(), getCompletionPercentage());
    }
}
