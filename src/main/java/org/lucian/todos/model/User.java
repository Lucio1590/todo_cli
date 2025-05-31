package org.lucian.todos.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user in the task management system.
 * Contains user credentials and profile information.
 */
public class User {
    
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    /**
     * Default constructor for User.
     * Sets created timestamp and default values.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
    
    /**
     * Constructor for database mapping with explicit timestamps.
     * Used when loading users from database.
     * 
     * @param createdAt the creation timestamp
     */
    public User(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
    
    /**
     * Constructor with username and email for User.
     * 
     * @param username the username, must not be null or empty
     * @param email the email address, must not be null or empty
     * @throws IllegalArgumentException if username or email is null or empty
     */
    public User(String username, String email) {
        this();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.username = username.trim();
        this.email = email.trim().toLowerCase();
    }
    
    /**
     * Full constructor for User.
     * 
     * @param username the username
     * @param email the email address
     * @param firstName the first name
     * @param lastName the last name
     */
    public User(String username, String email, String firstName, String lastName) {
        this();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.username = username.trim();
        this.email = email.trim().toLowerCase();
        this.firstName = firstName != null ? firstName.trim() : null;
        this.lastName = lastName != null ? lastName.trim() : null;
    }
    
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", active=" + active +
               ", createdAt=" + createdAt +
               ", lastLoginAt=" + lastLoginAt +
               '}';
    }

    // get; set;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        // email validation: must contain '@' with at least one character before and after
        String trimmedEmail = email.trim();
        int atIdx = trimmedEmail.indexOf('@');
        if (atIdx <= 0 || atIdx == trimmedEmail.length() - 1) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = trimmedEmail.toLowerCase();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Gets the user's full name.
     * 
     * @return the full name, or username if names are not available
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }
    
    /**
     * Gets the user's display name
     * @return the display name (full name if available, otherwise username)
     */
    public String getDisplayName() {
        String fullName = getFullName();
        return fullName.equals(username) ? username : fullName + " (" + username + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}

