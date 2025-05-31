package org.lucian.todos.cli.handler;

import java.util.Scanner;

import org.lucian.todos.cli.util.CLIUtils;
import org.lucian.todos.exceptions.AuthenticationException;
import org.lucian.todos.model.User;
import org.lucian.todos.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command handler for authentication-related operations in the CLI.
 * Handles user login, registration, logout, and profile management.
 * 

 */
public class AuthenticationCommandHandler implements CommandHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationCommandHandler.class);
    
    private final AuthenticationService authService;
    private final Scanner scanner;
    
    /**
     * Constructs a new AuthenticationCommandHandler.
     * 
     * @param authService the authentication service
     * @param scanner the scanner for user input
     */
    public AuthenticationCommandHandler(AuthenticationService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }
    
    /**
     * Handles user login process.
     * 
     * @return true if login was successful, false otherwise
     */
    public boolean handleLogin() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("User Login");
        
        try {
            // Get username
            String username = CLIUtils.getInput(scanner, "Username: ").trim();
            if (username.isEmpty()) {
                CLIUtils.printError("Username cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Get password (Note: In a real application, you'd want to hide password input)
            String password = CLIUtils.getInput(scanner, "Password: ").trim();
            if (password.isEmpty()) {
                CLIUtils.printError("Password cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Attempt login
            User user = authService.login(username, password);
            
            if (user != null) {
                CLIUtils.printSuccess("Login successful!");
                System.out.println("Welcome back, " + user.getFirstName() + "!");
                CLIUtils.waitForKeyPress(scanner);
                return true;
            } else {
                CLIUtils.printError("Invalid username or password.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
        } catch (AuthenticationException e) {
            CLIUtils.printError("Login failed: " + e.getMessage());
            CLIUtils.waitForKeyPress(scanner);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            CLIUtils.printError("An unexpected error occurred during login.");
            CLIUtils.waitForKeyPress(scanner);
            return false;
        }
    }
    
    /**
     * Handles user registration process.
     * 
     * @return true if registration was successful, false otherwise
     */
    public boolean handleRegistration() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("User Registration");
        
        try {
            System.out.println("Please provide the following information:");
            System.out.println();
            
            // Get username
            String username = CLIUtils.getInput(scanner, "Username: ").trim();
            if (username.isEmpty()) {
                CLIUtils.printError("Username cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Get email
            String email = CLIUtils.getInput(scanner, "Email: ").trim();
            if (email.isEmpty()) {
                CLIUtils.printError("Email cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Get password
            String password = CLIUtils.getInput(scanner, "Password: ").trim();
            if (password.isEmpty()) {
                CLIUtils.printError("Password cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Confirm password
            String confirmPassword = CLIUtils.getInput(scanner, "Confirm Password: ").trim();
            if (!password.equals(confirmPassword)) {
                CLIUtils.printError("Passwords do not match.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Get first name
            String firstName = CLIUtils.getInput(scanner, "First Name: ").trim();
            if (firstName.isEmpty()) {
                CLIUtils.printError("First name cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Get last name
            String lastName = CLIUtils.getInput(scanner, "Last Name: ").trim();
            if (lastName.isEmpty()) {
                CLIUtils.printError("Last name cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return false;
            }
            
            // Attempt registration
            User user = authService.register(username, email, password, firstName, lastName);
            
            CLIUtils.printSuccess("Registration successful!");
            System.out.println("Welcome, " + user.getFirstName() + "!");
            System.out.println("You are now logged in and can start using the Todo Management System.");
            CLIUtils.waitForKeyPress(scanner);
            return true;
            
        } catch (AuthenticationException e) {
            CLIUtils.printError("Registration failed: " + e.getMessage());
            CLIUtils.waitForKeyPress(scanner);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            CLIUtils.printError("An unexpected error occurred during registration.");
            CLIUtils.waitForKeyPress(scanner);
            return false;
        }
    }
    
    /**
     * Handles user logout process.
     */
    public void handleLogout() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Logout");
        
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                System.out.println("Are you sure you want to logout, " + currentUser.getFirstName() + "?");
                String confirm = CLIUtils.getInput(scanner, "Logout? (y/N): ").trim();
                
                if (confirm.toLowerCase().startsWith("y")) {
                    authService.logout();
                    CLIUtils.printSuccess("You have been logged out successfully.");
                    System.out.println("Thank you for using the Todo Management System!");
                } else {
                    CLIUtils.printInfo("Logout cancelled.");
                }
            } else {
                CLIUtils.printInfo("You are not currently logged in.");
            }
            
        } catch (Exception e) {
            logger.error("Error during logout", e);
            CLIUtils.printError("An error occurred during logout.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Displays the current user's profile information.
     */
    public void displayUserProfile() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("User Profile");
        
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                CLIUtils.printError("You are not currently logged in.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            System.out.println("Profile Information:");
            System.out.println();
            System.out.printf("Username: %s%s%s%n", CLIUtils.BOLD, currentUser.getUsername(), CLIUtils.RESET);
            System.out.printf("Email: %s%n", currentUser.getEmail());
            System.out.printf("Full Name: %s %s%n", currentUser.getFirstName(), currentUser.getLastName());
            System.out.printf("Account Status: %s%s%s%n", 
                currentUser.isActive() ? CLIUtils.GREEN : CLIUtils.RED,
                currentUser.isActive() ? "Active" : "Inactive",
                CLIUtils.RESET);
            System.out.printf("Member Since: %s%n", CLIUtils.formatDateTime(currentUser.getCreatedAt()));
            
            if (currentUser.getUpdatedAt() != null && !currentUser.getUpdatedAt().equals(currentUser.getCreatedAt())) {
                System.out.printf("Last Updated: %s%n", CLIUtils.formatDateTime(currentUser.getUpdatedAt()));
            }
            
        } catch (Exception e) {
            logger.error("Error displaying user profile", e);
            CLIUtils.printError("An error occurred while displaying your profile.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Handles user profile update process.
     */
    public void handleProfileUpdate() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Update Profile");
        
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                CLIUtils.printError("You are not currently logged in.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            System.out.println("Current Profile Information:");
            System.out.printf("Email: %s%n", currentUser.getEmail());
            System.out.printf("First Name: %s%n", currentUser.getFirstName());
            System.out.printf("Last Name: %s%n", currentUser.getLastName());
            System.out.println();
            System.out.println("Enter new information (press Enter to keep current value):");
            System.out.println();
            
            // Get new email
            String newEmail = CLIUtils.getInput(scanner, "Email [" + currentUser.getEmail() + "]: ").trim();
            if (newEmail.isEmpty()) {
                newEmail = currentUser.getEmail();
            }
            
            // Get new first name
            String newFirstName = CLIUtils.getInput(scanner, "First Name [" + currentUser.getFirstName() + "]: ").trim();
            if (newFirstName.isEmpty()) {
                newFirstName = currentUser.getFirstName();
            }
            
            // Get new last name
            String newLastName = CLIUtils.getInput(scanner, "Last Name [" + currentUser.getLastName() + "]: ").trim();
            if (newLastName.isEmpty()) {
                newLastName = currentUser.getLastName();
            }
            
            // Check if any changes were made
            boolean hasChanges = !newEmail.equals(currentUser.getEmail()) ||
                               !newFirstName.equals(currentUser.getFirstName()) ||
                               !newLastName.equals(currentUser.getLastName());
            
            if (!hasChanges) {
                CLIUtils.printInfo("No changes were made to your profile.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Confirm changes
            System.out.println();
            System.out.println("Summary of changes:");
            if (!newEmail.equals(currentUser.getEmail())) {
                System.out.printf("Email: %s → %s%n", currentUser.getEmail(), newEmail);
            }
            if (!newFirstName.equals(currentUser.getFirstName())) {
                System.out.printf("First Name: %s → %s%n", currentUser.getFirstName(), newFirstName);
            }
            if (!newLastName.equals(currentUser.getLastName())) {
                System.out.printf("Last Name: %s → %s%n", currentUser.getLastName(), newLastName);
            }
            
            String confirm = CLIUtils.getInput(scanner, "Save changes? (y/N): ").trim();
            if (confirm.toLowerCase().startsWith("y")) {
                authService.updateProfile(newEmail, newFirstName, newLastName);
                CLIUtils.printSuccess("Profile updated successfully!");
            } else {
                CLIUtils.printInfo("Profile update cancelled.");
            }
            
        } catch (AuthenticationException e) {
            CLIUtils.printError("Profile update failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during profile update", e);
            CLIUtils.printError("An unexpected error occurred while updating your profile.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Handles password change process.
     */
    public void handlePasswordChange() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Change Password");
        
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                CLIUtils.printError("You are not currently logged in.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Get current password
            String currentPassword = CLIUtils.getInput(scanner, "Current Password: ").trim();
            if (currentPassword.isEmpty()) {
                CLIUtils.printError("Current password cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Get new password
            String newPassword = CLIUtils.getInput(scanner, "New Password: ").trim();
            if (newPassword.isEmpty()) {
                CLIUtils.printError("New password cannot be empty.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Confirm new password
            String confirmPassword = CLIUtils.getInput(scanner, "Confirm New Password: ").trim();
            if (!newPassword.equals(confirmPassword)) {
                CLIUtils.printError("New passwords do not match.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Check if new password is same as current
            if (newPassword.equals(currentPassword)) {
                CLIUtils.printError("New password must be different from current password.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            // Attempt password change
            authService.changePassword(currentPassword, newPassword);
            CLIUtils.printSuccess("Password changed successfully!");
            
        } catch (AuthenticationException e) {
            CLIUtils.printError("Password change failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during password change", e);
            CLIUtils.printError("An unexpected error occurred while changing your password.");
        }
        
        CLIUtils.waitForKeyPress(scanner);
    }
    
    /**
     * Displays the authentication menu for logged-in users.
     */
    public void displayAuthenticatedMenu() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Account Management");
        
        try {
            User currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                CLIUtils.printError("You are not currently logged in.");
                CLIUtils.waitForKeyPress(scanner);
                return;
            }
            
            System.out.println("Logged in as: " + CLIUtils.BOLD + currentUser.getFirstName() + " " + 
                             currentUser.getLastName() + CLIUtils.RESET + " (" + currentUser.getUsername() + ")");
            System.out.println();
            System.out.println("Account Options:");
            System.out.println();
            System.out.println("  1. View Profile");
            System.out.println("  2. Update Profile");
            System.out.println("  3. Change Password");
            System.out.println("  4. Logout");
            System.out.println("  5. Back to Main Menu");
            System.out.println();
            
        } catch (Exception e) {
            logger.error("Error displaying authenticated menu", e);
            CLIUtils.printError("An error occurred while displaying the account menu.");
        }
    }
    
    /**
     * Handles the authenticated user menu choices.
     */
    public void handleAuthenticatedMenu() {
        while (true) {
            displayAuthenticatedMenu();
            String choice = CLIUtils.getInput(scanner, "Enter your choice: ").trim();
            
            try {
                switch (choice.toLowerCase()) {
                    case "1":
                    case "profile":
                    case "view":
                        displayUserProfile();
                        break;
                    case "2":
                    case "update":
                    case "edit":
                        handleProfileUpdate();
                        break;
                    case "3":
                    case "password":
                    case "change":
                        handlePasswordChange();
                        break;
                    case "4":
                    case "logout":
                        handleLogout();
                        if (!authService.isLoggedIn()) {
                            return; // User logged out, exit this menu
                        }
                        break;
                    case "5":
                    case "back":
                    case "return":
                    case "main":
                        return;
                    default:
                        CLIUtils.printError("Invalid choice. Please try again.");
                        CLIUtils.waitForKeyPress(scanner);
                }
            } catch (Exception e) {
                logger.error("Error handling authenticated menu choice", e);
                CLIUtils.printError("Error processing your request: " + e.getMessage());
                CLIUtils.waitForKeyPress(scanner);
            }
        }
    }
    
    /**
     * Displays the login/registration menu for unauthenticated users.
     */
    public void displayLoginMenu() {
        CLIUtils.clearScreen();
        CLIUtils.printHeader("Welcome to Todo Management System");
        
        System.out.println("Please choose an option to continue:");
        System.out.println();
        System.out.println("  1. Login");
        System.out.println("     └─ Access your existing account");
        System.out.println();
        System.out.println("  2. Register");
        System.out.println("     └─ Create a new account");
        System.out.println();
        System.out.println("  3. Exit");
        System.out.println("     └─ Quit the application");
        System.out.println();
        CLIUtils.printInfo("Note: You must be logged in to manage todos and projects.");
        System.out.println();
    }
    
    /**
     * Handles the login menu for unauthenticated users.
     * 
     * @return true if user successfully authenticated or wants to continue, false if user wants to exit
     */
    public boolean handleLoginMenu() {
        while (true) {
            displayLoginMenu();
            String choice = CLIUtils.getInput(scanner, "Enter your choice: ").trim();
            
            try {
                switch (choice.toLowerCase()) {
                    case "1":
                    case "login":
                        if (handleLogin()) {
                            return true; // User successfully logged in
                        }
                        break;
                    case "2":
                    case "register":
                    case "signup":
                        if (handleRegistration()) {
                            return true; // User successfully registered and logged in
                        }
                        break;
                    case "3":
                    case "exit":
                    case "quit":
                    case "q":
                        return false; // User wants to exit
                    default:
                        CLIUtils.printError("Invalid choice. Please try again.");
                        CLIUtils.waitForKeyPress(scanner);
                }
            } catch (Exception e) {
                logger.error("Error handling login menu choice", e);
                CLIUtils.printError("Error processing your request: " + e.getMessage());
                CLIUtils.waitForKeyPress(scanner);
            }
        }
    }
}
