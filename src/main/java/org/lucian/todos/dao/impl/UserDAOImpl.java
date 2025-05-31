package org.lucian.todos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.dao.UserDAO;
import org.lucian.todos.database.DatabaseManager;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLite implementation of UserDAO interface.
 * Handles all user persistence operations using SQLite database.
 */
public class UserDAOImpl implements UserDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    
    private final DatabaseManager databaseManager;
    
    public UserDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    @Override
    public User create(User user) throws DatabaseException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        logger.debug("Creating user: {}", user.getUsername());
        
        String sql = """
            INSERT INTO users (username, email, password_hash, first_name, last_name, 
                             active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setBoolean(6, user.isActive());
            statement.setTimestamp(7, Timestamp.valueOf(now));
            statement.setTimestamp(8, Timestamp.valueOf(now));
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating user failed, no rows affected");
            }
            
            // For SQLite, use last_insert_rowid() instead of getGeneratedKeys()
            try (PreparedStatement idStatement = connection.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = idStatement.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                } else {
                    throw new DatabaseException("Creating user failed, no ID obtained");
                }
            }
            
            logger.debug("Created user with ID: {}", user.getId());
            return user;
            
        } catch (SQLException e) {
            logger.error("Failed to create user", e);
            throw new DatabaseException("Failed to create user", e);
        }
    }
    
    @Override
    public Optional<User> findById(Long id) throws DatabaseException {
        if (id == null) {
            return Optional.empty();
        }
        
        logger.debug("Finding user by ID: {}", id);
        
        String sql = """
            SELECT id, username, email, password_hash, first_name, last_name, 
                   active, created_at, updated_at, last_login_at
            FROM users WHERE id = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("Found user: {}", user.getUsername());
                    return Optional.of(user);
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to find user by ID: {}", id, e);
            throw new DatabaseException("Failed to find user by ID", e);
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        
        logger.debug("Finding user by username: {}", username);
        
        String sql = """
            SELECT id, username, email, password_hash, first_name, last_name, 
                   active, created_at, updated_at, last_login_at
            FROM users WHERE username = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("Found user by username: {}", username);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to find user by username: {}", username, e);
            throw new DatabaseException("Failed to find user by username", e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        
        logger.debug("Finding user by email: {}", email);
        
        String sql = """
            SELECT id, username, email, password_hash, first_name, last_name, 
                   active, created_at, updated_at, last_login_at
            FROM users WHERE email = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email.trim().toLowerCase());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("Found user by email: {}", email);
                    return Optional.of(user);
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to find user by email: {}", email, e);
            throw new DatabaseException("Failed to find user by email", e);
        }
    }
    
    @Override
    public List<User> findAll() throws DatabaseException {
        logger.debug("Finding all users");
        
        String sql = """
            SELECT id, username, email, password_hash, first_name, last_name, 
                   active, created_at, updated_at, last_login_at
            FROM users ORDER BY username
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            
            logger.debug("Found {} users", users.size());
            return users;
            
        } catch (SQLException e) {
            logger.error("Failed to find all users", e);
            throw new DatabaseException("Failed to find all users", e);
        }
    }
    
    @Override
    public List<User> findAllActive() throws DatabaseException {
        logger.debug("Finding all active users");
        
        String sql = """
            SELECT id, username, email, password_hash, first_name, last_name, 
                   active, created_at, updated_at, last_login_at
            FROM users WHERE active = 1 ORDER BY username
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            
            logger.debug("Found {} active users", users.size());
            return users;
            
        } catch (SQLException e) {
            logger.error("Failed to find active users", e);
            throw new DatabaseException("Failed to find active users", e);
        }
    }
    
    @Override
    public User update(User user) throws DatabaseException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        
        logger.debug("Updating user: {}", user.getId());
        
        String sql = """
            UPDATE users SET username = ?, email = ?, password_hash = ?, 
                           first_name = ?, last_name = ?, active = ?, updated_at = ?
            WHERE id = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            user.setUpdatedAt(now);
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setBoolean(6, user.isActive());
            statement.setTimestamp(7, Timestamp.valueOf(now));
            statement.setLong(8, user.getId());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("User not found for update: " + user.getId());
            }
            
            logger.debug("Updated user: {}", user.getId());
            return user;
            
        } catch (SQLException e) {
            logger.error("Failed to update user: {}", user.getId(), e);
            throw new DatabaseException("Failed to update user", e);
        }
    }
    
    @Override
    public void updateLastLogin(Long userId) throws DatabaseException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        logger.debug("Updating last login for user: {}", userId);
        
        String sql = "UPDATE users SET last_login_at = ?, updated_at = ? WHERE id = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setTimestamp(1, Timestamp.valueOf(now));
            statement.setTimestamp(2, Timestamp.valueOf(now));
            statement.setLong(3, userId);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("User not found for last login update: " + userId);
            }
            
            logger.debug("Updated last login for user: {}", userId);
            
        } catch (SQLException e) {
            logger.error("Failed to update last login for user: {}", userId, e);
            throw new DatabaseException("Failed to update last login", e);
        }
    }
    
    @Override
    public boolean delete(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Deleting user: {}", id);
        
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            boolean deleted = affectedRows > 0;
            
            if (deleted) {
                logger.debug("Deleted user: {}", id);
            } else {
                logger.debug("User not found for deletion: {}", id);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Failed to delete user: {}", id, e);
            throw new DatabaseException("Failed to delete user", e);
        }
    }
    
    @Override
    public boolean deactivate(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Deactivating user: {}", id);
        
        String sql = "UPDATE users SET active = 0, updated_at = ? WHERE id = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(2, id);
            
            int affectedRows = statement.executeUpdate();
            boolean deactivated = affectedRows > 0;
            
            if (deactivated) {
                logger.debug("Deactivated user: {}", id);
            } else {
                logger.debug("User not found for deactivation: {}", id);
            }
            
            return deactivated;
            
        } catch (SQLException e) {
            logger.error("Failed to deactivate user: {}", id, e);
            throw new DatabaseException("Failed to deactivate user", e);
        }
    }
    
    @Override
    public boolean reactivate(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Reactivating user: {}", id);
        
        String sql = "UPDATE users SET active = 1, updated_at = ? WHERE id = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(2, id);
            
            int affectedRows = statement.executeUpdate();
            boolean reactivated = affectedRows > 0;
            
            if (reactivated) {
                logger.debug("Reactivated user: {}", id);
            } else {
                logger.debug("User not found for reactivation: {}", id);
            }
            
            return reactivated;
            
        } catch (SQLException e) {
            logger.error("Failed to reactivate user: {}", id, e);
            throw new DatabaseException("Failed to reactivate user", e);
        }
    }
    
    @Override
    public boolean exists(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Checking if user exists: {}", id);
        
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        
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
            logger.error("Failed to check user existence: {}", id, e);
            throw new DatabaseException("Failed to check user existence", e);
        }
    }
    
    @Override
    public boolean usernameExists(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        logger.debug("Checking if username exists: {}", username);
        
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, username.trim());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1) > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Failed to check username existence: {}", username, e);
            throw new DatabaseException("Failed to check username existence", e);
        }
    }
    
    @Override
    public boolean emailExists(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        logger.debug("Checking if email exists: {}", email);
        
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email.trim().toLowerCase());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1) > 0;
                }
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Failed to check email existence: {}", email, e);
            throw new DatabaseException("Failed to check email existence", e);
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        logger.debug("Counting all users");
        
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                long count = resultSet.getLong(1);
                logger.debug("Total users count: {}", count);
                return count;
            }
            return 0;
            
        } catch (SQLException e) {
            logger.error("Failed to count users", e);
            throw new DatabaseException("Failed to count users", e);
        }
    }
    
    @Override
    public long countActive() throws DatabaseException {
        logger.debug("Counting active users");
        
        String sql = "SELECT COUNT(*) FROM users WHERE active = 1";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                long count = resultSet.getLong(1);
                logger.debug("Active users count: {}", count);
                return count;
            }
            return 0;
            
        } catch (SQLException e) {
            logger.error("Failed to count active users", e);
            throw new DatabaseException("Failed to count active users", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a User object.
     * 
     * @param resultSet the ResultSet to map from
     * @return the mapped User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        User user = new User(createdAt != null ? createdAt.toLocalDateTime() : LocalDateTime.now());
        
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setActive(resultSet.getBoolean("active"));
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        Timestamp lastLoginAt = resultSet.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            user.setLastLoginAt(lastLoginAt.toLocalDateTime());
        }
        
        return user;
    }
}
