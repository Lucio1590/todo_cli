package org.lucian.todos.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.dao.ProjectDAO;
import org.lucian.todos.database.DatabaseManager;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLite implementation of ProjectDAO interface.
 * Handles all project persistence operations using SQLite database.
 * 

 */
public class ProjectDAOImpl implements ProjectDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDAOImpl.class);
    
    private final DatabaseManager databaseManager;
    
    public ProjectDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    @Override
    public Project create(Project project) throws DatabaseException {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        
        logger.debug("Creating project: {}", project.getName());
        
        String sql = """
            INSERT INTO projects (name, description, start_date, end_date, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setDate(3, project.getStartDate() != null ? Date.valueOf(project.getStartDate()) : null);
            statement.setDate(4, project.getEndDate() != null ? Date.valueOf(project.getEndDate()) : null);
            statement.setTimestamp(5, Timestamp.valueOf(now));
            statement.setTimestamp(6, Timestamp.valueOf(now));
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating project failed, no rows affected");
            }
            
            // For SQLite, use last_insert_rowid() instead of getGeneratedKeys()
            try (PreparedStatement idStatement = connection.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = idStatement.executeQuery()) {
                if (rs.next()) {
                    project.setId(rs.getLong(1));
                } else {
                    throw new DatabaseException("Creating project failed, no ID obtained");
                }
            }
            
            logger.debug("Created project with ID: {}", project.getId());
            return project;
            
        } catch (SQLException e) {
            logger.error("Failed to create project", e);
            throw new DatabaseException("Failed to create project", e);
        }
    }
    
    @Override
    public Optional<Project> findById(Long id) throws DatabaseException {
        if (id == null) {
            return Optional.empty();
        }
        
        logger.debug("Finding project by ID: {}", id);
        
        String sql = """
            SELECT id, name, description, start_date, end_date, created_at, updated_at
            FROM projects 
            WHERE id = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToProject(resultSet));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to find project by ID: {}", id, e);
            throw new DatabaseException("Failed to find project", e);
        }
    }
    
    @Override
    public List<Project> findAll() throws DatabaseException {
        logger.debug("Finding all projects");
        
        String sql = """
            SELECT id, name, description, start_date, end_date, created_at, updated_at
            FROM projects 
            ORDER BY created_at DESC
        """;
        
        return executeQueryForProjectList(sql);
    }
    
    @Override
    public List<Project> findByName(String name) throws DatabaseException {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        logger.debug("Finding projects by name: {}", name);
        
        String sql = """
            SELECT id, name, description, start_date, end_date, created_at, updated_at
            FROM projects 
            WHERE LOWER(name) LIKE LOWER(?)
            ORDER BY name ASC
        """;
        
        return executeQueryForProjectList(sql, "%" + name.trim() + "%");
    }
    
    @Override
    public List<Project> findCompleted() throws DatabaseException {
        logger.debug("Finding completed projects");
        
        String sql = """
            SELECT p.id, p.name, p.description, p.start_date, p.end_date, p.created_at, p.updated_at
            FROM projects p
            WHERE NOT EXISTS (
                SELECT 1 FROM todos t 
                WHERE t.project_id = p.id AND t.status NOT IN ('COMPLETED', 'CANCELLED')
            )
            AND EXISTS (
                SELECT 1 FROM todos t WHERE t.project_id = p.id
            )
            ORDER BY p.created_at DESC
        """;
        
        return executeQueryForProjectList(sql);
    }
    
    @Override
    public List<Project> findActive() throws DatabaseException {
        logger.debug("Finding active projects");
        
        String sql = """
            SELECT DISTINCT p.id, p.name, p.description, p.start_date, p.end_date, p.created_at, p.updated_at
            FROM projects p
            INNER JOIN todos t ON p.id = t.project_id
            WHERE t.status NOT IN ('COMPLETED', 'CANCELLED')
            ORDER BY p.created_at DESC
        """;
        
        return executeQueryForProjectList(sql);
    }
    
    @Override
    public Project update(Project project) throws DatabaseException {
        if (project == null || project.getId() == null) {
            throw new IllegalArgumentException("Project and project ID cannot be null");
        }
        
        logger.debug("Updating project: {} (ID: {})", project.getName(), project.getId());
        
        String sql = """
            UPDATE projects 
            SET name = ?, description = ?, start_date = ?, end_date = ?, updated_at = ?
            WHERE id = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, project.getName());
            statement.setString(2, project.getDescription());
            statement.setDate(3, project.getStartDate() != null ? Date.valueOf(project.getStartDate()) : null);
            statement.setDate(4, project.getEndDate() != null ? Date.valueOf(project.getEndDate()) : null);
            statement.setTimestamp(5, Timestamp.valueOf(now));
            statement.setLong(6, project.getId());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Updating project failed, project not found");
            }
            
            logger.debug("Updated project with ID: {}", project.getId());
            return project;
            
        } catch (SQLException e) {
            logger.error("Failed to update project: {}", project.getId(), e);
            throw new DatabaseException("Failed to update project", e);
        }
    }
    
    @Override
    public boolean delete(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Deleting project with ID: {}", id);
        
        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            
            try {
                // First delete all todos in the project
                String deleteTodosSql = "DELETE FROM todos WHERE project_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteTodosSql)) {
                    statement.setLong(1, id);
                    int deletedTodos = statement.executeUpdate();
                    logger.debug("Deleted {} todos for project {}", deletedTodos, id);
                }
                
                // Then delete the project
                String deleteProjectSql = "DELETE FROM projects WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteProjectSql)) {
                    statement.setLong(1, id);
                    int affectedRows = statement.executeUpdate();
                    
                    connection.commit();
                    
                    boolean deleted = affectedRows > 0;
                    if (deleted) {
                        logger.debug("Deleted project with ID: {}", id);
                    } else {
                        logger.debug("Project not found for deletion: {}", id);
                    }
                    
                    return deleted;
                }
                
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            logger.error("Failed to delete project: {}", id, e);
            throw new DatabaseException("Failed to delete project", e);
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        logger.debug("Counting all projects");
        
        String sql = "SELECT COUNT(*) FROM projects";
        
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            logger.error("Failed to count projects", e);
            throw new DatabaseException("Failed to count projects", e);
        }
    }
    
    @Override
    public boolean exists(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Checking if project exists: {}", id);
        
        String sql = "SELECT COUNT(*) FROM projects WHERE id = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1) > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Failed to check project existence: {}", id, e);
            throw new DatabaseException("Failed to check project existence", e);
        }
    }
    
    // Helper methods
    
    private List<Project> executeQueryForProjectList(String sql, Object... parameters) throws DatabaseException {
        List<Project> projects = new ArrayList<>();
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    projects.add(mapResultSetToProject(resultSet));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Failed to execute query for project list", e);
            throw new DatabaseException("Failed to query projects", e);
        }
        
        return projects;
    }
    
    private Project mapResultSetToProject(ResultSet resultSet) throws SQLException {
        Project project = new Project();
        
        project.setId(resultSet.getLong("id"));
        project.setName(resultSet.getString("name"));
        project.setDescription(resultSet.getString("description"));
        
        Date startDate = resultSet.getDate("start_date");
        if (startDate != null) {
            project.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = resultSet.getDate("end_date");
        if (endDate != null) {
            project.setEndDate(endDate.toLocalDate());
        }
        
        return project;
    }
}
