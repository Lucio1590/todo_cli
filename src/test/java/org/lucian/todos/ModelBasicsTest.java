package org.lucian.todos;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.exceptions.ProjectNotFoundException;
import org.lucian.todos.exceptions.TodoNotFoundException;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.Project;
import org.lucian.todos.model.RecurringTodo;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;
import org.lucian.todos.model.User;

public class ModelBasicsTest {
    @Test
    void testUserCreationAndValidation() {
        User user = new User("lucian", "lucian@example.com", "Lucian", "Diaconu");
        assertEquals("lucian", user.getUsername());
        assertEquals("lucian@example.com", user.getEmail());
        assertTrue(user.isActive());
        assertNotNull(user.getCreatedAt());
        assertEquals("Lucian Diaconu", user.getFullName());
    }

    @Test
    void testProjectCreationAndValidation() {
        Project project = new Project("Test Project", "desc", LocalDate.now(), LocalDate.now().plusDays(10));
        assertEquals("Test Project", project.getName());
        assertEquals("desc", project.getDescription());
        assertFalse(project.isCompleted());
        assertDoesNotThrow(project::validate);
    }

    @Test
    void testTodoCreationAndStatus() {
        Todo todo = new Todo("Task title", "desc", LocalDate.now().plusDays(1), Priority.HIGH);
        assertEquals("Task title", todo.getTitle());
        assertEquals(Priority.HIGH, todo.getPriority());
        assertEquals(TodoStatus.TODO, todo.getStatus());
        todo.markInProgress();
        assertEquals(TodoStatus.IN_PROGRESS, todo.getStatus());
        todo.markCompleted();
        assertEquals(TodoStatus.COMPLETED, todo.getStatus());
    }

    @Test
    void testRecurringTodoFunctionality() {
        RecurringTodo recurring = new RecurringTodo("Daily", "desc", LocalDate.now(), Priority.MEDIUM, Period.ofDays(1));
        assertEquals(1, recurring.getCurrentOccurrence());
        assertTrue(recurring.hasMoreOccurrences());
        recurring.markCompleted();
        assertEquals(2, recurring.getCurrentOccurrence());
        assertEquals(TodoStatus.TODO, recurring.getStatus());
    }

    @Test
    void testExceptionMessages() {
        ProjectNotFoundException pex = new ProjectNotFoundException(123L);
        assertTrue(pex.getUserFriendlyMessage().contains("123"));
        TodoNotFoundException tex = new TodoNotFoundException(456L);
        assertTrue(tex.getUserFriendlyMessage().contains("456"));
        DatabaseException dbex = new DatabaseException("db error");
        assertTrue(dbex.getUserFriendlyMessage().toLowerCase().contains("database error"));
    }
}
