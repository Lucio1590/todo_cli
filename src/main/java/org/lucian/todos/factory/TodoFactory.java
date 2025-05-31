package org.lucian.todos.factory;

import java.time.LocalDate;
import java.time.Period;

import org.lucian.todos.model.Priority;
import org.lucian.todos.model.RecurringTodo;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating different types of todos.
 * Implements the Factory pattern to abstract todo creation logic.
 */
public class TodoFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(TodoFactory.class);
    
    /**
     * Creates a todo of the specified type with just a title.
     * 
     * @param type the type of todo to create
     * @param title the todo title
     * @return the created todo
     * @throws IllegalArgumentException if type is null or title is invalid
     */
    public static Todo createTodo(TodoType type, String title) {
        if (type == null) {
            throw new IllegalArgumentException("Todo type cannot be null");
        }
        
        validateTodoParameters(title);
        
        logger.debug("Creating {} with title: {}", type, title);
        
        return switch (type) {
            case SIMPLE -> new Todo(title);
            case RECURRING -> new RecurringTodo(title);
        };
    }
    
    /**
     * Creates a simple todo with full details.
     * 
     * @param title the todo title
     * @param description the todo description
     * @param dueDate the due date
     * @param priority the todo priority
     * @return the created simple todo
     */
    public static Todo createSimpleTodo(String title, String description, 
                                       LocalDate dueDate, Priority priority) {
        validateTodoParameters(title);
        logger.debug("Creating simple todo: {} with due date: {}", title, dueDate);
        return new Todo(title, description, dueDate, priority);
    }
    
    /**
     * Creates a recurring todo with full details.
     * 
     * @param title the todo title
     * @param description the todo description
     * @param dueDate the first due date
     * @param priority the todo priority
     * @param recurringInterval the interval between occurrences
     * @return the created recurring todo
     */
    public static RecurringTodo createRecurringTodo(String title, String description,
                                                   LocalDate dueDate, Priority priority,
                                                   Period recurringInterval) {
        validateTodoParameters(title);
        logger.debug("Creating recurring todo: {} with interval: {}", title, recurringInterval);
        return new RecurringTodo(title, description, dueDate, priority, recurringInterval);
    }
    
    /**
     * Creates a recurring todo with max occurrences.
     * 
     * @param title the todo title
     * @param description the todo description
     * @param dueDate the first due date
     * @param priority the todo priority
     * @param recurringInterval the interval between occurrences
     * @param maxOccurrences the maximum number of occurrences
     * @return the created recurring todo
     */
    public static RecurringTodo createRecurringTodo(String title, String description,
                                                   LocalDate dueDate, Priority priority,
                                                   Period recurringInterval, int maxOccurrences) {
        logger.debug("Creating recurring todo: {} with {} occurrences", title, maxOccurrences);
        
        RecurringTodo todo = createRecurringTodo(title, description, dueDate, priority, recurringInterval);
        todo.setMaxOccurrences(maxOccurrences);
        return todo;
    }
    
    /**
     * Creates a daily recurring todo.
     * 
     * @param title the todo title
     * @param dueDate the first due date
     * @return the created daily recurring todo
     */
    public static RecurringTodo createDailyTodo(String title, LocalDate dueDate) {
        logger.debug("Creating daily recurring todo: {}", title);
        return createRecurringTodo(title, null, dueDate, Priority.MEDIUM, Period.ofDays(1));
    }
    
    /**
     * Creates a weekly recurring todo.
     * 
     * @param title the todo title
     * @param dueDate the first due date
     * @return the created weekly recurring todo
     */
    public static RecurringTodo createWeeklyTodo(String title, LocalDate dueDate) {
        logger.debug("Creating weekly recurring todo: {}", title);
        return createRecurringTodo(title, null, dueDate, Priority.MEDIUM, Period.ofWeeks(1));
    }
    
    /**
     * Creates a monthly recurring todo.
     * 
     * @param title the todo title
     * @param dueDate the first due date
     * @return the created monthly recurring todo
     */
    public static RecurringTodo createMonthlyTodo(String title, LocalDate dueDate) {
        logger.debug("Creating monthly recurring todo: {}", title);
        return createRecurringTodo(title, null, dueDate, Priority.MEDIUM, Period.ofMonths(1));
    }
    
    /**
     * Creates a todo with high priority and today's due date.
     * 
     * @param title the todo title
     * @return the created urgent todo
     */
    public static Todo createUrgentTodo(String title) {
        logger.debug("Creating urgent todo: {}", title);
        return new Todo(title, null, LocalDate.now(), Priority.URGENT);
    }
    
    /**
     * Creates a todo from a template todo by copying its properties.
     * 
     * @param template the template todo to copy from
     * @param newTitle the title for the new todo
     * @return the created todo based on template
     * @throws IllegalArgumentException if template is null
     */
    public static Todo createFromTemplate(Todo template, String newTitle) {
        if (template == null) {
            throw new IllegalArgumentException("Template todo cannot be null");
        }
        
        logger.debug("Creating todo from template: {} -> {}", template.getTitle(), newTitle);
        
        if (template instanceof RecurringTodo recurringTemplate) {
            RecurringTodo newTodo = new RecurringTodo(newTitle);
            newTodo.setDescription(recurringTemplate.getDescription());
            newTodo.setPriority(recurringTemplate.getPriority());
            newTodo.setRecurringInterval(recurringTemplate.getRecurringInterval());
            newTodo.setMaxOccurrences(recurringTemplate.getMaxOccurrences());
            return newTodo;
        } else {
            Todo newTodo = new Todo(newTitle);
            newTodo.setDescription(template.getDescription());
            newTodo.setDueDate(template.getDueDate());
            newTodo.setPriority(template.getPriority());
            return newTodo;
        }
    }
    
    /**
     * Gets the todo type for a given todo instance.
     * 
     * @param todo the todo to determine type for
     * @return the todo type
     * @throws IllegalArgumentException if todo is null
     */
    public static TodoType getTodoType(Todo todo) {
        if (todo == null) {
            throw new IllegalArgumentException("Todo cannot be null");
        }
        
        if (todo instanceof RecurringTodo) {
            return TodoType.RECURRING;
        } else {
            return TodoType.SIMPLE;
        }
    }
    
    /**
     * Validates todo creation parameters.
     * 
     * @param title the todo title
     * @throws IllegalArgumentException if parameters are invalid
     */
    private static void validateTodoParameters(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be null or empty");
        }
        
        if (title.length() > 255) {
            throw new IllegalArgumentException("Todo title cannot exceed 255 characters");
        }
    }
}
