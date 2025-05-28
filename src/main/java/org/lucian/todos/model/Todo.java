package org.lucian.todos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Todo {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private TodoStatus status;
    private Long projectId;
    private Long userId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor for Todo.
     * Sets created timestamp and default values.
     */
    public Todo() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.priority = Priority.MEDIUM;
        this.status = TodoStatus.TODO;
    }

    /**
     * Constructor with only  Todo.
     * 
     * @param title the todo title, must not be null or empty
     * @throws IllegalArgumentException if title is null or empty
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Todo(String title) {
        this();
        // NOTE: added suppres warning for constructor call as i want to ensure using the setter for checking
        setTitle(title);
    }

    /**
     * Full constructor for Task.
     * 
     * @param title the todo title
     * @param description the todo description
     * @param dueDate the due date for the todo
     * @param priority the todo priority
     */
    public Todo(String title, String description, LocalDate dueDate, Priority priority) {
        this(title);
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : Priority.MEDIUM;
    }



    //GET;SET;
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be null or empty");
        }
        this.title = title.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.updatedAt = LocalDateTime.now();
    }
    
    public TodoStatus getStatus() {
        return status;
    }
    
    public void setStatus(TodoStatus status) {
        this.status = status != null ? status : TodoStatus.TODO;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
