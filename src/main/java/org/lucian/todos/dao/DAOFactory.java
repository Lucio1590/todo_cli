package org.lucian.todos.dao;

import org.lucian.todos.dao.impl.ProjectDAOImpl;
import org.lucian.todos.dao.impl.TodoDAOImpl;
import org.lucian.todos.dao.impl.UserDAOImpl;
import org.lucian.todos.database.DatabaseManager;

/**
 * Factory class for creating DAO instances.
 * Provides centralized DAO creation and dependency injection.
 */
public class DAOFactory {
    
    private final DatabaseManager databaseManager;
    private final TodoDAO todoDAO;
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private static DAOFactory instance;
    
    /**
     * Creates a new DAO factory with the specified database manager.
     * 
     * @param databaseManager the database manager to use
     */
    public DAOFactory(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.todoDAO = new TodoDAOImpl(databaseManager);
        this.projectDAO = new ProjectDAOImpl(databaseManager);
        this.userDAO = new UserDAOImpl(databaseManager);
    }
    
    /**
     * Gets the singleton instance of DAOFactory.
     * 
     * @return the DAOFactory instance
     */
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory(DatabaseManager.getInstance());
        }
        return instance;
    }
    
    /**
     * Gets the TodoDAO instance.
     * 
     * @return the TodoDAO instance
     */
    public TodoDAO getTodoDAO() {
        return todoDAO;
    }
    
    /**
     * Gets the ProjectDAO instance.
     * 
     * @return the ProjectDAO instance
     */
    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }
    
    /**
     * Gets the UserDAO instance.
     * 
     * @return the UserDAO instance
     */
    public UserDAO getUserDAO() {
        return userDAO;
    }
    
    /**
     * Gets the DatabaseManager instance.
     * 
     * @return the DatabaseManager instance
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
