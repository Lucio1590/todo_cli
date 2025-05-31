package org.lucian.todos;

import org.lucian.todos.cli.TodoManagementCLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Todo Management System.
 * 
 * This class now launches the full-featured CLI interface for the todo management system.
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    /**
     * Main method to start the Todo Management System.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting Todo Management System...");
        
        try {
            // Launch the CLI interface
            TodoManagementCLI cli = new TodoManagementCLI();
            cli.start();
        } catch (Exception e) {
            logger.error("Failed to start Todo Management System", e);
            System.err.println("Failed to start Todo Management System.");
            System.err.println("Please check the logs for technical details.");
            System.exit(1);
        } finally {
            logger.info("Todo Management System shutdown complete.");
        }
    }
    
    /**
     * Simple utility method to get the application name.
     * This method exists primarily for testing purposes.
     * 
     * @return the application name
     */
    public static String getApplicationName() {
        return "Todo Management System";
    }
}