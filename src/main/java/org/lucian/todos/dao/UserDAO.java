package org.lucian.todos.dao;

import java.util.List;
import java.util.Optional;

import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.User;

/**
 * Data Access Object interface for User operations.
 * Provides CRUD operations and specialized queries for user management.
 */
public interface UserDAO {
    
    /**
     * Creates a new user in the database.
     * 
     * @param user the user to create, must not be null
     * @return the created user with generated ID
     * @throws DatabaseException if creation fails
     * @throws IllegalArgumentException if user is null
     */
    User create(User user) throws DatabaseException;
    
    /**
     * Retrieves a user by ID.
     * 
     * @param id the user ID to search for
     * @return an Optional containing the user if found, empty otherwise
     * @throws DatabaseException if retrieval fails
     */
    Optional<User> findById(Long id) throws DatabaseException;
    
    /**
     * Retrieves a user by username.
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found, empty otherwise
     * @throws DatabaseException if retrieval fails
     */
    Optional<User> findByUsername(String username) throws DatabaseException;
    
    /**
     * Retrieves a user by email address.
     * 
     * @param email the email to search for
     * @return an Optional containing the user if found, empty otherwise
     * @throws DatabaseException if retrieval fails
     */
    Optional<User> findByEmail(String email) throws DatabaseException;
    
    /**
     * Retrieves all users from the database.
     * 
     * @return a list of all users, empty list if none found
     * @throws DatabaseException if retrieval fails
     */
    List<User> findAll() throws DatabaseException;
    
    /**
     * Retrieves all active users from the database.
     * 
     * @return a list of active users, empty list if none found
     * @throws DatabaseException if retrieval fails
     */
    List<User> findAllActive() throws DatabaseException;
    
    /**
     * Updates an existing user in the database.
     * 
     * @param user the user to update, must not be null and must have an ID
     * @return the updated user
     * @throws DatabaseException if update fails or user doesn't exist
     * @throws IllegalArgumentException if user is null or has no ID
     */
    User update(User user) throws DatabaseException;
    
    /**
     * Updates the last login timestamp for a user.
     * 
     * @param userId the ID of the user to update
     * @throws DatabaseException if update fails or user doesn't exist
     */
    void updateLastLogin(Long userId) throws DatabaseException;
    
    /**
     * Deletes a user by ID.
     * 
     * @param id the ID of the user to delete
     * @return true if user was deleted, false if user didn't exist
     * @throws DatabaseException if deletion fails
     */
    boolean delete(Long id) throws DatabaseException;
    
    /**
     * Deactivates a user instead of deleting them.
     * This preserves data integrity while preventing login.
     * 
     * @param id the ID of the user to deactivate
     * @return true if user was deactivated, false if user didn't exist
     * @throws DatabaseException if deactivation fails
     */
    boolean deactivate(Long id) throws DatabaseException;
    
    /**
     * Reactivates a previously deactivated user.
     * 
     * @param id the ID of the user to reactivate
     * @return true if user was reactivated, false if user didn't exist
     * @throws DatabaseException if reactivation fails
     */
    boolean reactivate(Long id) throws DatabaseException;
    
    /**
     * Checks if a user exists with the given ID.
     * 
     * @param id the user ID to check
     * @return true if user exists, false otherwise
     * @throws DatabaseException if check fails
     */
    boolean exists(Long id) throws DatabaseException;
    
    /**
     * Checks if a username is already taken.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     * @throws DatabaseException if check fails
     */
    boolean usernameExists(String username) throws DatabaseException;
    
    /**
     * Checks if an email is already registered.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     * @throws DatabaseException if check fails
     */
    boolean emailExists(String email) throws DatabaseException;
    
    /**
     * Counts the total number of users.
     * 
     * @return the total number of users
     * @throws DatabaseException if count fails
     */
    long count() throws DatabaseException;
    
    /**
     * Counts the number of active users.
     * 
     * @return the number of active users
     * @throws DatabaseException if count fails
     */
    long countActive() throws DatabaseException;
}
