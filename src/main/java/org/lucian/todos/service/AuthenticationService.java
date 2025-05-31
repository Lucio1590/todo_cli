package org.lucian.todos.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.lucian.todos.dao.UserDAO;
import org.lucian.todos.exceptions.AuthenticationException;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for handling user authentication and authorization.
 * Provides secure user registration, login, logout, and session management.
 * 
 * @author Lucian Diaconu
 */
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 32;
    
    private final UserDAO userDAO;
    private User currentUser;
    private LocalDateTime sessionStartTime;
    
    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    /**
     * Registers a new user in the system.
     * 
     * @param username the desired username
     * @param email the user's email address
     * @param password the user's password (plain text)
     * @param firstName the user's first name (optional)
     * @param lastName the user's last name (optional)
     * @return the created user
     * @throws AuthenticationException if registration fails
     */
    public User register(String username, String email, String password, 
                        String firstName, String lastName) throws AuthenticationException {
        
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters long");
        }
        
        logger.debug("Registering new user: {}", username);
        
        try {
            // Check if username already exists
            if (userDAO.usernameExists(username)) {
                throw new AuthenticationException("Username already exists: " + username);
            }
            
            // Check if email already exists
            if (userDAO.emailExists(email)) {
                throw new AuthenticationException("Email already exists: " + email);
            }
            
            // Create new user
            User user = new User(username, email, firstName, lastName);
            String passwordHash = hashPassword(password);
            user.setPasswordHash(passwordHash);
            
            User createdUser = userDAO.create(user);
            logger.info("Successfully registered user: {}", username);
            
            return createdUser;
            
        } catch (DatabaseException e) {
            logger.error("Database error during user registration", e);
            throw new AuthenticationException("Registration failed due to system error", e);
        }
    }
    
    /**
     * Authenticates a user with username and password.
     * 
     * @param username the username
     * @param password the password (plain text)
     * @return the authenticated user
     * @throws AuthenticationException if authentication fails
     */
    public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }
        
        logger.debug("Attempting login for user: {}", username);
        
        try {
            Optional<User> userOpt = userDAO.findByUsername(username);
            if (userOpt.isEmpty()) {
                // Don't reveal that username doesn't exist
                logger.warn("Login attempt with non-existent username: {}", username);
                throw new AuthenticationException("Invalid username or password");
            }
            
            User user = userOpt.get();
            
            // Check if user is active
            if (!user.isActive()) {
                logger.warn("Login attempt with deactivated account: {}", username);
                throw new AuthenticationException("Account is deactivated. Please contact support.");
            }
            
            // Verify password
            if (!verifyPassword(password, user.getPasswordHash())) {
                logger.warn("Invalid password for user: {}", username);
                throw new AuthenticationException("Invalid username or password");
            }
            
            // Update last login time
            userDAO.updateLastLogin(user.getId());
            user.setLastLoginAt(LocalDateTime.now());
            
            // Set current session
            this.currentUser = user;
            this.sessionStartTime = LocalDateTime.now();
            
            logger.info("Successfully logged in user: {}", username);
            return user;
            
        } catch (DatabaseException e) {
            logger.error("Database error during login", e);
            throw new AuthenticationException("Login failed due to system error", e);
        }
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("Logging out user: {}", currentUser.getUsername());
            this.currentUser = null;
            this.sessionStartTime = null;
        }
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Gets the currently logged-in user.
     * 
     * @return the current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the session start time.
     * 
     * @return the session start time, or null if no session is active
     */
    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }
    
    /**
     * Changes the password for the current user.
     * 
     * @param oldPassword the current password
     * @param newPassword the new password
     * @throws AuthenticationException if password change fails
     */
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("No user is currently logged in");
        }
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new AuthenticationException("Current password cannot be empty");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new AuthenticationException("New password cannot be empty");
        }
        if (newPassword.length() < 6) {
            throw new AuthenticationException("New password must be at least 6 characters long");
        }
        if (oldPassword.equals(newPassword)) {
            throw new AuthenticationException("New password must be different from current password");
        }
        
        logger.debug("Changing password for user: {}", currentUser.getUsername());
        
        try {
            // Verify current password
            if (!verifyPassword(oldPassword, currentUser.getPasswordHash())) {
                throw new AuthenticationException("Current password is incorrect");
            }
            
            // Update password
            String newPasswordHash = hashPassword(newPassword);
            currentUser.setPasswordHash(newPasswordHash);
            userDAO.update(currentUser);
            
            logger.info("Successfully changed password for user: {}", currentUser.getUsername());
            
        } catch (DatabaseException e) {
            logger.error("Database error during password change", e);
            throw new AuthenticationException("Password change failed due to system error", e);
        }
    }
    
    /**
     * Updates the current user's profile information.
     * 
     * @param firstName the new first name
     * @param lastName the new last name
     * @param email the new email address
     * @throws AuthenticationException if update fails
     */
    public void updateProfile(String firstName, String lastName, String email) throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("No user is currently logged in");
        }
        
        logger.debug("Updating profile for user: {}", currentUser.getUsername());
        
        try {
            // Check if email is changing and if new email already exists
            if (email != null && !email.equals(currentUser.getEmail())) {
                if (userDAO.emailExists(email)) {
                    throw new AuthenticationException("Email already exists: " + email);
                }
            }
            
            // Update user information
            if (firstName != null) {
                currentUser.setFirstName(firstName);
            }
            if (lastName != null) {
                currentUser.setLastName(lastName);
            }
            if (email != null) {
                currentUser.setEmail(email);
            }
            
            userDAO.update(currentUser);
            
            logger.info("Successfully updated profile for user: {}", currentUser.getUsername());
            
        } catch (DatabaseException e) {
            logger.error("Database error during profile update", e);
            throw new AuthenticationException("Profile update failed due to system error", e);
        }
    }
    
    /**
     * Requires that a user is logged in.
     * 
     * @throws AuthenticationException if no user is logged in
     */
    public void requireLogin() throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("This action requires you to be logged in");
        }
    }
    
    /**
     * Hashes a password using SHA-256 with a random salt.
     * 
     * @param password the password to hash
     * @return the hashed password with salt
     */
    private String hashPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Combine salt and hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(saltAndHash);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing algorithm not available", e);
        }
    }

    /**
     * Checks if a string contains only valid Base64 characters.
     *
     * @param input the string to check
     * @return true if the string contains only valid Base64 characters, false otherwise
     */
    private boolean isValidBase64(String input) {
        if (input == null) {
            return false;
        }

        // Standard Base64 alphabet characters
        String base64Pattern = "^[A-Za-z0-9+/]*={0,2}$";
        return input.matches(base64Pattern);
    }


    /**
     * Verifies a password against a stored hash.
     * 
     * @param password the password to verify
     * @param storedHash the stored hash to verify against
     * @return true if password matches, false otherwise
     */
    private boolean verifyPassword(String password, String storedHash) {
        try {
            // Validate the stored hash
            if (storedHash == null || storedHash.isEmpty()) {
                logger.error("Stored password hash is null or empty");
                return false;
            }

            // Check if the stored hash contains valid Base64 characters
            if (!isValidBase64(storedHash)) {
                logger.error("Stored password hash is not valid Base64");
                return false;
            }

            // Decode stored hash
            byte[] saltAndHash = Base64.getDecoder().decode(storedHash);

            // Validate decoded data length
            if (saltAndHash.length <= SALT_LENGTH) {
                logger.error("Decoded hash data is too short");
                return false;
            }

            // Extract salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);

            // Extract hash
            byte[] storedPasswordHash = new byte[saltAndHash.length - SALT_LENGTH];
            System.arraycopy(saltAndHash, SALT_LENGTH, storedPasswordHash, 0, storedPasswordHash.length);

            // Hash provided password with extracted salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Compare hashes
            return MessageDigest.isEqual(storedPasswordHash, hashedPassword);

        } catch (Exception e) {
            logger.error("Error verifying password: {}", e.getMessage());
            return false;
        }
    }
}
