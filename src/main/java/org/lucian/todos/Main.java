package org.lucian.todos;
import org.lucian.todos.exceptions.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        logger.info("Starting Todo Application...");

        try {
            System.out.println("Welcome to the TODoo Management System!");
            // TODO: implement the main application where the CLI is started
            throw new NotImplementedException("This application is not implemented yet.");
        } catch (NotImplementedException e) {
            logger.error("Failed to start Todo Management System", e);
            System.err.println("Failed to start Todo Management System.");
            System.err.println("Please check the logs for technical details.");
            System.exit(1);

            logger.info("Task Management System shutdown complete.");
        }
    }

    /**
     * Returns the name of the application.
     *
     * @return Stting: the name of the application
     */
    public static String getApplicationName() {
        return "Task Management System";
    }
}
