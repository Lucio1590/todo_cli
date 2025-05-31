package org.lucian.todos.database;

import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DatabaseManagerTest {

    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        databaseManager = DatabaseManager.getTestInstance();
    }

    @Test
    @DisplayName("Test singleton instance creation")
    void testSingletonInstanceCreation() {
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();
        assertSame(instance1, instance2, "Singleton instances should be the same");
    }

    @Test
    @DisplayName("Test database connection")
    void testDatabaseConnection() {
        assertDoesNotThrow(() -> {
            Connection connection = databaseManager.getConnection();
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(1), "Connection should be valid");
        }, "Getting a database connection should not throw an exception");
    }

    @Test
    @DisplayName("Test database initialization")
    void testDatabaseInitialization() {
        assertDoesNotThrow(() -> {
            databaseManager.getConnection();
        }, "Database initialization should not throw an exception");
    }

    @Test
    @DisplayName("Test migration check")
    void testMigrationCheck() {
        assertDoesNotThrow(() -> {
            Connection connection = databaseManager.getConnection();
            boolean needsMigration = connection.createStatement().executeQuery("PRAGMA table_info(tasks)").next();
            assertFalse(needsMigration, "Migration should not be needed for fresh in-memory database");
        }, "Checking migration should not throw an exception");
    }

    @AfterEach
    void tearDown() {
        databaseManager = null;
    }
}
