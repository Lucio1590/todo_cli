package org.lucian.todos.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.dao.TodoDAO;
import org.lucian.todos.database.DatabaseManager;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.RecurringTodo;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQLite implementation of TodoDAO interface.
 * Handles all todo persistence operations using SQLite database.
 */
public class TodoDAOImpl implements TodoDAO {

    private static final Logger logger = LoggerFactory.getLogger(TodoDAOImpl.class);
    
    private final DatabaseManager databaseManager;
    
    public TodoDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    @Override
    public Todo create(Todo todo) throws DatabaseException {
        if (todo == null) {
            throw new IllegalArgumentException("Todo cannot be null");
        }
        
        logger.debug("Creating todo: {}", todo.getTitle());
        
        String sql = """
            INSERT INTO todos (title, description, due_date, priority, status, project_id, user_id, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setDate(3, todo.getDueDate() != null ? Date.valueOf(todo.getDueDate()) : null);
            statement.setString(4, todo.getPriority().name());
            statement.setString(5, todo.getStatus().name());
            statement.setObject(6, todo.getProjectId());
            statement.setObject(7, todo.getUserId());
            statement.setTimestamp(8, Timestamp.valueOf(now));
            statement.setTimestamp(9, Timestamp.valueOf(now));
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating todo failed, no rows affected");
            }
            
            // For SQLite, use last_insert_rowid() instead of getGeneratedKeys()
            try (PreparedStatement idStatement = connection.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = idStatement.executeQuery()) {
                if (rs.next()) {
                    todo.setId(rs.getLong(1));
                } else {
                    throw new DatabaseException("Creating todo failed, no ID obtained");
                }
            }
            
            // Handle recurring todo specific data
            if (todo instanceof RecurringTodo recurringTodo) {
                createRecurringTodoData(connection, recurringTodo);
            }
            
            logger.debug("Created todo with ID: {}", todo.getId());
            return todo;
            
        } catch (SQLException e) {
            logger.error("Failed to create todo", e);
            throw new DatabaseException("Failed to create todo", e);
        }
    }
    
    @Override
    public Optional<Todo> findById(Long id) throws DatabaseException {
        if (id == null) {
            return Optional.empty();
        }
        
        logger.debug("Finding todo by ID: {}", id);
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            WHERE t.id = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToTodo(resultSet));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to find todo by ID: {}", id, e);
            throw new DatabaseException("Failed to find todo", e);
        }
    }
    
    @Override
    public List<Todo> findAll() throws DatabaseException {
        logger.debug("Finding all todos");
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            ORDER BY t.created_at DESC
        """;
        
        return executeQueryForTodoList(sql);
    }
    
    @Override
    public List<Todo> findByProjectId(Long projectId) throws DatabaseException {
        logger.debug("Finding todos by project ID: {}", projectId);
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            WHERE t.project_id = ?
            ORDER BY t.created_at DESC
        """;
        
        return executeQueryForTodoList(sql, projectId);
    }
    
    @Override
    public List<Todo> findByStatus(TodoStatus status) throws DatabaseException {
        if (status == null) {
            return new ArrayList<>();
        }
        
        logger.debug("Finding todos by status: {}", status);
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            WHERE t.status = ?
            ORDER BY t.created_at DESC
        """;
        
        return executeQueryForTodoList(sql, status.name());
    }
    
    @Override
    public List<Todo> findByPriority(Priority priority) throws DatabaseException {
        if (priority == null) {
            return new ArrayList<>();
        }
        
        logger.debug("Finding todos by priority: {}", priority);
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            WHERE t.priority = ?
            ORDER BY t.created_at DESC
        """;
        
        return executeQueryForTodoList(sql, priority.name());
    }
    
    @Override
    public List<Todo> findDueBefore(LocalDate date) throws DatabaseException {
        if (date == null) {
            return new ArrayList<>();
        }
        
        logger.debug("Finding todos due before: {}", date);
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            WHERE t.due_date <= ?
            ORDER BY t.due_date ASC
        """;
        
        return executeQueryForTodoList(sql, Date.valueOf(date));
    }
    
    @Override
    public List<Todo> findOverdue() throws DatabaseException {
        logger.debug("Finding overdue todos");
        
        String sql = """
            SELECT t.*, rt.recurring_interval_days, rt.max_occurrences, rt.current_occurrence, rt.next_due_date
            FROM todos t
            LEFT JOIN recurring_todos rt ON t.id = rt.todo_id
            WHERE t.due_date < ? AND t.status NOT IN ('COMPLETED', 'CANCELLED')
            ORDER BY t.due_date ASC
        """;
        
        return executeQueryForTodoList(sql, Date.valueOf(LocalDate.now()));
    }
    
    @Override
    public Todo update(Todo todo) throws DatabaseException {
        if (todo == null || todo.getId() == null) {
            throw new IllegalArgumentException("Todo and todo ID cannot be null");
        }
        
        logger.debug("Updating todo: {} (ID: {})", todo.getTitle(), todo.getId());
        
        String sql = """
            UPDATE todos 
            SET title = ?, description = ?, due_date = ?, priority = ?, status = ?, 
                project_id = ?, updated_at = ?
            WHERE id = ?
        """;
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setDate(3, todo.getDueDate() != null ? Date.valueOf(todo.getDueDate()) : null);
            statement.setString(4, todo.getPriority().name());
            statement.setString(5, todo.getStatus().name());
            statement.setObject(6, todo.getProjectId());
            statement.setTimestamp(7, Timestamp.valueOf(now));
            statement.setLong(8, todo.getId());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Updating todo failed, todo not found");
            }
            
            // Handle recurring todo specific data
            if (todo instanceof RecurringTodo recurringTodo) {
                updateRecurringTodoData(connection, recurringTodo);
            }
            
            logger.debug("Updated todo with ID: {}", todo.getId());
            return todo;
            
        } catch (SQLException e) {
            logger.error("Failed to update todo: {}", todo.getId(), e);
            throw new DatabaseException("Failed to update todo", e);
        }
    }
    
    @Override
    public boolean delete(Long id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        logger.debug("Deleting todo with ID: {}", id);
        
        String sql = "DELETE FROM todos WHERE id = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            boolean deleted = affectedRows > 0;
            
            if (deleted) {
                logger.debug("Deleted todo with ID: {}", id);
            } else {
                logger.debug("Todo not found for deletion: {}", id);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Failed to delete todo: {}", id, e);
            throw new DatabaseException("Failed to delete todo", e);
        }
    }
    
    @Override
    public long count() throws DatabaseException {
        logger.debug("Counting all todos");
        
        String sql = "SELECT COUNT(*) FROM todos";
        
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            logger.error("Failed to count todos", e);
            throw new DatabaseException("Failed to count todos", e);
        }
    }
    
    @Override
    public long countByStatus(TodoStatus status) throws DatabaseException {
        if (status == null) {
            return 0;
        }
        
        logger.debug("Counting todos by status: {}", status);
        
        String sql = "SELECT COUNT(*) FROM todos WHERE status = ?";
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, status.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return 0;
            }
            
        } catch (SQLException e) {
            logger.error("Failed to count todos by status: {}", status, e);
            throw new DatabaseException("Failed to count todos by status", e);
        }
    }
    
    // Helper methods
    
    private List<Todo> executeQueryForTodoList(String sql, Object... parameters) throws DatabaseException {
        List<Todo> todos = new ArrayList<>();
        
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    todos.add(mapResultSetToTodo(resultSet));
                }
            }
            
        } catch (SQLException e) {
            logger.error("Failed to execute query for todo list", e);
            throw new DatabaseException("Failed to query todos", e);
        }
        
        return todos;
    }
    
    private Todo mapResultSetToTodo(ResultSet resultSet) throws SQLException {
        Todo todo;
        
        // Check if this is a recurring todo
        if (resultSet.getObject("recurring_interval_days") != null) {
            RecurringTodo recurringTodo = new RecurringTodo();
            
            int intervalDays = resultSet.getInt("recurring_interval_days");
            recurringTodo.setRecurringInterval(Period.ofDays(intervalDays));
            recurringTodo.setMaxOccurrences(resultSet.getInt("max_occurrences"));
            
            // Set current occurrence using reflection to bypass validation
            try {
                var field = RecurringTodo.class.getDeclaredField("currentOccurrence");
                field.setAccessible(true);
                field.setInt(recurringTodo, resultSet.getInt("current_occurrence"));
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | SQLException e) {
                logger.warn("Failed to set current occurrence", e);
            }
            
            Date nextDueDate = resultSet.getDate("next_due_date");
            if (nextDueDate != null) {
                // Set next due date using reflection
                try {
                    var field = RecurringTodo.class.getDeclaredField("nextDueDate");
                    field.setAccessible(true);
                    field.set(recurringTodo, nextDueDate.toLocalDate());
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                    logger.warn("Failed to set next due date", e);
                }
            }
            
            todo = recurringTodo;
        } else {
            todo = new Todo();
        }
        
        // Set common todo properties
        todo.setId(resultSet.getLong("id"));
        todo.setTitle(resultSet.getString("title"));
        todo.setDescription(resultSet.getString("description"));
        
        Date dueDate = resultSet.getDate("due_date");
        if (dueDate != null) {
            todo.setDueDate(dueDate.toLocalDate());
        }
        
        todo.setPriority(Priority.valueOf(resultSet.getString("priority")));
        todo.setStatus(TodoStatus.valueOf(resultSet.getString("status")));
        
        Long projectId = resultSet.getObject("project_id", Long.class);
        todo.setProjectId(projectId);
        
        return todo;
    }
    
    private void createRecurringTodoData(Connection connection, RecurringTodo recurringTodo) 
            throws SQLException {
        String sql = """
            INSERT INTO recurring_todos (todo_id, recurring_interval_days, max_occurrences, 
                                       current_occurrence, next_due_date)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, recurringTodo.getId());
            statement.setInt(2, recurringTodo.getRecurringInterval().getDays());
            statement.setInt(3, recurringTodo.getMaxOccurrences());
            statement.setInt(4, recurringTodo.getCurrentOccurrence());
            statement.setDate(5, recurringTodo.getNextDueDate() != null ? 
                Date.valueOf(recurringTodo.getNextDueDate()) : null);
            
            statement.executeUpdate();
        }
    }
    
    private void updateRecurringTodoData(Connection connection, RecurringTodo recurringTodo) 
            throws SQLException {
        String sql = """
            UPDATE recurring_todos 
            SET recurring_interval_days = ?, max_occurrences = ?, current_occurrence = ?, 
                next_due_date = ?
            WHERE todo_id = ?
        """;
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recurringTodo.getRecurringInterval().getDays());
            statement.setInt(2, recurringTodo.getMaxOccurrences());
            statement.setInt(3, recurringTodo.getCurrentOccurrence());
            statement.setDate(4, recurringTodo.getNextDueDate() != null ? 
                Date.valueOf(recurringTodo.getNextDueDate()) : null);
            statement.setLong(5, recurringTodo.getId());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                // If no rows were updated, insert the recurring todo data
                createRecurringTodoData(connection, recurringTodo);
            }
        }
    }
}
