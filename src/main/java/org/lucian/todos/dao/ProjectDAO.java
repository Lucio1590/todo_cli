package org.lucian.todos.dao;

import java.util.List;
import java.util.Optional;

import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.Project;

/**
 * Data Access Object interface for Project entity operations.
 * Defines the contract for project persistence operations.
 * 

 */
public interface ProjectDAO {
    
    /**
     * Creates a new project in the database.
     * 
     * @param project the project to create (ID will be generated)
     * @return the created project with generated ID
     * @throws DatabaseException if creation fails
     */
    Project create(Project project) throws DatabaseException;
    
    /**
     * Finds a project by its ID.
     * 
     * @param id the project ID
     * @return Optional containing the project if found, empty otherwise
     * @throws DatabaseException if query fails
     */
    Optional<Project> findById(Long id) throws DatabaseException;
    
    /**
     * Retrieves all projects from the database.
     * 
     * @return list of all projects
     * @throws DatabaseException if query fails
     */
    List<Project> findAll() throws DatabaseException;
    
    /**
     * Finds projects by name (case-insensitive partial match).
     * 
     * @param name the project name to search for
     * @return list of projects matching the name
     * @throws DatabaseException if query fails
     */
    List<Project> findByName(String name) throws DatabaseException;
    
    /**
     * Finds all completed projects (all todos completed).
     * 
     * @return list of completed projects
     * @throws DatabaseException if query fails
     */
    List<Project> findCompleted() throws DatabaseException;
    
    /**
     * Finds all active projects (has incomplete todos).
     * 
     * @return list of active projects
     * @throws DatabaseException if query fails
     */
    List<Project> findActive() throws DatabaseException;
    
    /**
     * Updates an existing project in the database.
     * 
     * @param project the project to update
     * @return the updated project
     * @throws DatabaseException if update fails
     */
    Project update(Project project) throws DatabaseException;
    
    /**
     * Deletes a project by its ID.
     * Note: This will also delete all associated todos.
     * 
     * @param id the project ID
     * @return true if project was deleted, false if not found
     * @throws DatabaseException if deletion fails
     */
    boolean delete(Long id) throws DatabaseException;
    
    /**
     * Counts the total number of projects.
     * 
     * @return the total project count
     * @throws DatabaseException if query fails
     */
    long count() throws DatabaseException;
    
    /**
     * Checks if a project exists with the given ID.
     * 
     * @param id the project ID
     * @return true if project exists, false otherwise
     * @throws DatabaseException if query fails
     */
    boolean exists(Long id) throws DatabaseException;
}
