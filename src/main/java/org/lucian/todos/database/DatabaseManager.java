package org.lucian.todos.database;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.lucian.todos.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String DEFAULT_DATABASE_URL = "jdbc:sqlite:todos.db";
    private static final String TEST_DATABASE_URL = "jdbc:sqlite::memory:";

    private final String databaseUrl;
    private static DatabaseManager instance;

    /**
     * Private constructor for singleton pattern.
     *
     * @param databaseUrl the database URL to connect to
     */
    private DatabaseManager(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        try {
            initializeDatabase();
        } catch (DatabaseException e) {
            logger.error("Failed to initialize database in constructor", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Gets the singleton instance for production database.
     *
     * @return the database manager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(DEFAULT_DATABASE_URL);
        }
        return instance;
    }
    /**
     * Gets the singleton instance for testing with in-memory database.
     *
     * @return the test database manager instance
     */
    public static synchronized DatabaseManager getTestInstance() {
        return new DatabaseManager(TEST_DATABASE_URL);
    }

    /**
     * Gets a database connection.
     *
     * @return a database connection
     * @throws DatabaseException if connection cannot be established
     */
    public Connection getConnection() throws DatabaseException {
        try {
            Connection connection = DriverManager.getConnection(databaseUrl);
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to get database connection", e);
            throw new DatabaseException("Unable to connect to database", e);
        }
    }

    /**
     * Checks if the database needs migration from old schema to new schema.
     *
     * @param connection the database connection
     * @return true if migration is needed, false if fresh install
     * @throws SQLException if check fails
     */
    private boolean checkIfMigrationNeeded(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery("PRAGMA table_info(todos)")) {
        boolean todosExists = false;
        boolean hasUserId = false;

        while (resultSet.next()) {
            todosExists = true;
            String columnName = resultSet.getString("name");
            if ("user_id".equals(columnName)) {
                hasUserId = true;
                break;
            }
        }

        // If todos table exists but doesn't have user_id, migration is needed
        return todosExists && !hasUserId;
    } catch (SQLException e) {
        // If error checking table info, assume fresh install
        logger.debug("Error checking table structure, assuming fresh install: {}", e.getMessage());
        return false;
    }
}
    /**
     * Initializes the database schema.
     *
     * @throws DatabaseException if schema creation fails
     */
    private void initializeDatabase() throws DatabaseException {
        logger.info("Initializing database with URL: {}", databaseUrl);

        try (Connection connection = DriverManager.getConnection(databaseUrl);
             Statement statement = connection.createStatement()) {

            // Enable foreign key constraints
            statement.execute("PRAGMA foreign_keys = ON");

            // Check if this is a fresh database or needs migration
            boolean needsMigration = checkIfMigrationNeeded(connection);

            if (needsMigration) {
                logger.info("Existing database detected, performing migration...");
                performDatabaseMigration(connection);
            } else {
                logger.info("Creating fresh database schema...");
                createFreshSchema(connection);
            }

            logger.info("Database schema initialized successfully");

        } catch (SQLException e) {
            logger.error("Failed to initialize database schema", e);
            throw new DatabaseException("Failed to initialize database", e);
        }
    }

    /**
     * Performs database migration from old schema to new schema with user support.
     *
     * @param connection the database connection
     * @throws SQLException if migration fails
     */
    private void performDatabaseMigration(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            logger.info("Starting database migration...");

            // Create users table first
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL UNIQUE,
                            email TEXT NOT NULL UNIQUE,
                            password_hash TEXT NOT NULL,
                            first_name TEXT,
                            last_name TEXT,
                            active BOOLEAN NOT NULL DEFAULT 1,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            last_login_at DATETIME
                        )
                    """);


            // Create default admin user for migration
            createDefaultAdminUser(statement);

            logger.info("Created default admin user for migration");

            // Check if projects table exists and migrate it
            if (tableExists(connection, "projects")) {
                // Add user_id column to existing projects table
                try {
                    statement.execute("ALTER TABLE projects ADD COLUMN user_id INTEGER NOT NULL DEFAULT 1");
                    logger.info("Added user_id column to projects table");
                } catch (SQLException e) {
                    if (!e.getMessage().contains("duplicate column")) {
                        throw e;
                    }
                    logger.debug("user_id column already exists in projects table");
                }

                // Add foreign key constraint (SQLite doesn't support adding FK constraints directly)
                // We'll create indexes instead for now
            } else {
                // Create projects table with user support
                statement.execute("""
                            CREATE TABLE projects (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                name TEXT NOT NULL,
                                description TEXT,
                                start_date DATE,
                                end_date DATE,
                                user_id INTEGER NOT NULL DEFAULT 1,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                            )
                        """);
            }

            // Check if todos table exists and migrate it
            if (tableExists(connection, "todos")) {
                // Add user_id column to existing todos table
                try {
                    statement.execute("ALTER TABLE todos ADD COLUMN user_id INTEGER NOT NULL DEFAULT 1");
                    logger.info("Added user_id column to todos table");
                } catch (SQLException e) {
                    if (!e.getMessage().contains("duplicate column")) {
                        throw e;
                    }
                    logger.debug("user_id column already exists in todos table");
                }
            } else {
                // Create todos table with user support
                statement.execute("""
                            CREATE TABLE todos (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                title TEXT NOT NULL,
                                description TEXT,
                                due_date DATE,
                                priority TEXT NOT NULL DEFAULT 'MEDIUM',
                                status TEXT NOT NULL DEFAULT 'TODO',
                                project_id INTEGER,
                                user_id INTEGER NOT NULL DEFAULT 1,
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                            )
                        """);
            }

            // Create recurring_todos table if it doesn't exist
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS recurring_todos (
                            todo_id INTEGER PRIMARY KEY,
                            recurring_interval_days INTEGER NOT NULL,
                            max_occurrences INTEGER NOT NULL DEFAULT 2147483647,
                            current_occurrence INTEGER NOT NULL DEFAULT 1,
                            next_due_date DATE,
                            FOREIGN KEY (todo_id) REFERENCES todos(id) ON DELETE CASCADE
                        )
                    """);

            // Create indexes
            createIndexes(statement);

            logger.info("Database migration completed successfully");
        }
    }

    /**
     * Creates fresh database schema for new installations.
     *
     * @param connection the database connection
     * @throws SQLException if schema creation fails
     */
    private void createFreshSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create users table first (referenced by other tables)
            logger.info("Executing schema creation for users table...");
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL UNIQUE,
                            email TEXT NOT NULL UNIQUE,
                            password_hash TEXT NOT NULL,
                            first_name TEXT,
                            last_name TEXT,
                            active BOOLEAN NOT NULL DEFAULT 1,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            last_login_at DATETIME
                        )
                    """);
            logger.info("Users table creation statement executed successfully.");

            // Create projects table
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS projects (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            description TEXT,
                            start_date DATE,
                            end_date DATE,
                            user_id INTEGER NOT NULL,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                        )
                    """);

            // Create todos table
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS todos (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            title TEXT NOT NULL,
                            description TEXT,
                            due_date DATE,
                            priority TEXT NOT NULL DEFAULT 'MEDIUM',
                            status TEXT NOT NULL DEFAULT 'TODO',
                            project_id INTEGER,
                            user_id INTEGER NOT NULL,
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                        )
                    """);

            // Create recurring_todos table for additional recurring todo data
            statement.execute("""
                        CREATE TABLE IF NOT EXISTS recurring_todos (
                            todo_id INTEGER PRIMARY KEY,
                            recurring_interval_days INTEGER NOT NULL,
                            max_occurrences INTEGER NOT NULL DEFAULT 2147483647,
                            current_occurrence INTEGER NOT NULL DEFAULT 1,
                            next_due_date DATE,
                            FOREIGN KEY (todo_id) REFERENCES todos(id) ON DELETE CASCADE
                        )
                    """);

            // Create indexes
            createIndexes(statement);

            // Create default admin user for fresh installations
            createDefaultAdminUser(statement);
        }
    }

    /**
     * Creates database indexes for better performance.
     *
     * @param statement the SQL statement executor
     * @throws SQLException if index creation fails
     */
    private void createIndexes(Statement statement) throws SQLException {
        statement.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_users_active ON users(active)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_project_id ON todos(project_id)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_user_id ON todos(user_id)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_status ON todos(status)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_priority ON todos(priority)");
        statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_due_date ON todos(due_date)");
    }

    /**
     * Creates a default admin user for fresh database installations.
     * This ensures there's always an admin user available for initial setup.
     *
     * @param statement the SQL statement executor
     * @throws SQLException if user creation fails
     */
    private void createDefaultAdminUser(Statement statement) throws SQLException {
        logger.info("Creating default admin user for fresh installation...");

        // Generate password hash using the same logic as AuthenticationService
        String adminPasswordHash = generateAdminPasswordHash("admin");

        statement.execute(String.format("""
                    INSERT OR IGNORE INTO users (id, username, email, password_hash, first_name, last_name, active)
                    VALUES (1, 'admin', 'admin@localhost.com', '%s', 'Admin', 'User', 1)
                """, adminPasswordHash));

        logger.info("Default admin user created successfully (username: 'admin', password: 'admin')");
        logger.warn("SECURITY WARNING: Please change the default admin password immediately after first login!");
    }

    /**
     * Generates a password hash for the admin user using SHA-256 + salt, base64-encoded.
     * This duplicates AuthenticationService.hashPassword logic for DB bootstrapping.
     */
    private String generateAdminPasswordHash(String password) {
        try {
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] salt = new byte[32];
            random.nextBytes(salt);
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            return java.util.Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate admin password hash", e);
            throw new RuntimeException("Failed to generate admin password hash", e);
        }
    }

    /**
     * Checks if a table exists in the database.
     *
     * @param connection the database connection
     * @param tableName  the name of the table to check
     * @return true if table exists, false otherwise
     * @throws SQLException if check fails
     */
    private boolean tableExists(Connection connection, String tableName) throws SQLException {
    try (var ps = connection.prepareStatement(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {
        ps.setString(1, tableName);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}

    /**
     * Closes all database connections and shuts down the database.
     * This should be called when the application is shutting down.
     */
    public void shutdown() {
        logger.info("Shutting down database manager");
        // SQLite doesn't require explicit shutdown
    }

    /**
     * Executes a database health check.
     *
     * @return true if database is accessible
     */
    public boolean isHealthy() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1");
            return true;

        } catch (Exception e) {
            logger.warn("Database health check failed", e);
            return false;
        }
    }

    /**
     * Gets the database URL.
     *
     * @return the database URL
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

}
