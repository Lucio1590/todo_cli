package org.lucian.todos.exceptions;

/**
 * Exception thrown when authentication or authorization operations fail.
 * This includes login failures, invalid credentials, permission denied, etc.
 */
public class AuthenticationException extends Exception {
    

    /**
    * Serial version UID for serialization.
    * This is used to ensure that a loaded class corresponds exactly to a serialized object.
    * The `serialVersionUID` is a unique identifier for Serializable classes in Java. It is used during the deserialization process to ensure that a loaded class corresponds exactly to a serialized object.  
    * It's a best practice to explicitly declare `serialVersionUID` in serializable classes to avoid unexpected issues during deserialization.
    *
    * If not declared, Java will generate one at runtime based on class details, which can change if the class is modified, potentially breaking serialization compatibility.
    */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new authentication exception with null as its detail message.
     */
    public AuthenticationException() {
        super();
    }
    
    /**
     * Constructs a new authentication exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new authentication exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new authentication exception with the specified cause.
     * 
     * @param cause the cause
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
