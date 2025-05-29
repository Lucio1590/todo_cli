package org.lucian.todos.exceptions;

/**
 * Exception thrown when a project is not found.
 */
public class ProjectNotFoundException extends TodoManagementException {
    
    private final Long projectId;
    
    /**
     * Constructs a ProjectNotFoundException with the specified project ID.
     * 
     * @param projectId the ID of the project that was not found
     */
    public ProjectNotFoundException(Long projectId) {
        super("Project not found: " + projectId);
        this.projectId = projectId;
    }
    
    /**
     * Constructs a ProjectNotFoundException with the specified message and project ID.
     * 
     * @param message the detail message
     * @param projectId the ID of the project that was not found
     */
    public ProjectNotFoundException(String message, Long projectId) {
        super(message);
        this.projectId = projectId;
    }
    
    /**
     * Constructs a ProjectNotFoundException with the specified message, project ID, and cause.
     * 
     * @param message the detail message
     * @param projectId the ID of the project that was not found
     * @param cause the cause of this exception
     */
    public ProjectNotFoundException(String message, Long projectId, Throwable cause) {
        super(message, cause);
        this.projectId = projectId;
    }
    
    /**
     * Gets the project ID that was not found.
     * 
     * @return the project ID
     */
    public Long getProjectId() {
        return projectId;
    }
    
    @Override
    public String getUserFriendlyMessage() {
        return projectId != null 
            ? "The requested project (ID: " + projectId + ") could not be found."
            : "The requested project could not be found.";
    }
}