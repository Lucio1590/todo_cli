package org.lucian.todos.cli.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized error handling utility for the CLI application.
 * Provides user-friendly error messages while logging technical details.
 * 
 * @author Lucian Diaconu
 * @since 1.1
 */
public class ErrorHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    
    /**
     * Handles unexpected exceptions by showing user-friendly messages and logging technical details.
     * 
     * @param e the exception that occurred
     * @param context description of what operation was being performed
     */
    public static void handleUnexpectedError(Exception e, String context) {
        logger.error("Unexpected error during {}", context, e);
        CLIUtils.printError("An unexpected error occurred while " + context + ". Please try again.");
    }
    
    /**
     * Handles business logic exceptions by showing their message to the user and logging the details.
     * 
     * @param e the business exception that occurred
     * @param context description of what operation was being performed
     */
    public static void handleBusinessError(Exception e, String context) {
        logger.warn("Business error during {}: {}", context, e.getMessage());
        CLIUtils.printError(e.getMessage());
    }
    
    /**
     * Handles database exceptions with user-friendly messages.
     * 
     * @param e the database exception that occurred
     * @param context description of what operation was being performed
     */
    public static void handleDatabaseError(Exception e, String context) {
        logger.error("Database error during {}", context, e);
        CLIUtils.printError("A database error occurred while " + context + ". Please check your data and try again.");
    }
    
    /**
     * Handles authentication exceptions with appropriate user messages.
     * 
     * @param e the authentication exception that occurred
     * @param context description of what operation was being performed
     */
    public static void handleAuthenticationError(Exception e, String context) {
        logger.warn("Authentication error during {}: {}", context, e.getMessage());
        CLIUtils.printError("Authentication failed: " + e.getMessage());
    }
    
    /**
     * Handles system startup/initialization errors.
     * 
     * @param e the exception that occurred during startup
     * @param component the component that failed to initialize
     */
    public static void handleStartupError(Exception e, String component) {
        logger.error("Failed to initialize {}", component, e);
        CLIUtils.printError("Failed to start " + component + ": " + e.getMessage());
        CLIUtils.printError("Please check your system configuration and try again.");
    }
}
