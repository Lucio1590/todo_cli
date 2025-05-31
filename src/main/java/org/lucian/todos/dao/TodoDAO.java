package org.lucian.todos.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;

/**
 * Data Access Object interface for Todo entity operations.
 * Defines the contract for todo persistence operations.
 */
public interface TodoDAO {
    
    /**
     * Creates a new todo in the database.
     * 
     * @param todo the todo to create (ID will be generated)
     * @return the created todo with generated ID
     * @throws DatabaseException if creation fails
     */
    Todo create(Todo todo) throws DatabaseException;
    
    /**
     * Finds a todo by its ID.
     * 
     * @param id the todo ID
     * @return Optional containing the todo if found, empty otherwise
     * @throws DatabaseException if query fails
     */
    Optional<Todo> findById(Long id) throws DatabaseException;
    
    /**
     * Retrieves all todos from the database.
     * 
     * @return list of all todos
     * @throws DatabaseException if query fails
     */
    List<Todo> findAll() throws DatabaseException;
    
    /**
     * Finds all todos belonging to a specific project.
     * 
     * @param projectId the project ID
     * @return list of todos in the project
     * @throws DatabaseException if query fails
     */
    List<Todo> findByProjectId(Long projectId) throws DatabaseException;
    
    /**
     * Finds all todos with a specific status.
     * 
     * @param status the todo status
     * @return list of todos with the specified status
     * @throws DatabaseException if query fails
     */
    List<Todo> findByStatus(TodoStatus status) throws DatabaseException;
    
    /**
     * Finds all todos with a specific priority.
     * 
     * @param priority the todo priority
     * @return list of todos with the specified priority
     * @throws DatabaseException if query fails
     */
    List<Todo> findByPriority(Priority priority) throws DatabaseException;
    
    /**
     * Finds all todos due on or before a specific date.
     * 
     * @param date the due date threshold
     * @return list of todos due on or before the date
     * @throws DatabaseException if query fails
     */
    List<Todo> findDueBefore(LocalDate date) throws DatabaseException;
    
    /**
     * Finds all overdue todos (due date in the past and not completed).
     * 
     * @return list of overdue todos
     * @throws DatabaseException if query fails
     */
    List<Todo> findOverdue() throws DatabaseException;
    
    /**
     * Updates an existing todo in the database.
     * 
     * @param todo the todo to update
     * @return the updated todo
     * @throws DatabaseException if update fails
     */
    Todo update(Todo todo) throws DatabaseException;
    
    /**
     * Deletes a todo by its ID.
     * 
     * @param id the todo ID
     * @return true if todo was deleted, false if not found
     * @throws DatabaseException if deletion fails
     */
    boolean delete(Long id) throws DatabaseException;
    
    /**
     * Counts the total number of todos.
     * 
     * @return the total todo count
     * @throws DatabaseException if query fails
     */
    long count() throws DatabaseException;
    
    /**
     * Counts todos by status.
     * 
     * @param status the todo status
     * @return the count of todos with the specified status
     * @throws DatabaseException if query fails
     */
    long countByStatus(TodoStatus status) throws DatabaseException;
}
