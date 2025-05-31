package org.lucian.todos;

import java.time.LocalDate;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.Project;
import org.lucian.todos.model.RecurringTodo;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.User;

public class ModelAdvancedTest {
    @Test
    void testUserInvalidCreation() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> new User(null, "a@b.com"));
        assertTrue(ex1.getMessage().contains("Username cannot be null or empty"));
        
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> new User(" ", "a@b.com"));
        assertTrue(ex2.getMessage().contains("Username cannot be null or empty"));
        
        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> new User("lucian", "bademail"));
        assertTrue(ex3.getMessage().contains("Invalid email format"));
    }

    @Test
    void testProjectInvalidDates() {
        LocalDate now = LocalDate.now();
        IllegalStateException ex1 =  assertThrows(IllegalStateException.class, () -> {
            Project p = new Project("P", "desc", now.plusDays(2), now);
            p.validate();
        });
        assertTrue(ex1.getMessage().contains("Project start date cannot be after end date"));
    }

    @Test
    void testTodoValidationAndOverdue() {
        Todo todo = new Todo("Test", null, LocalDate.now().minusDays(1), Priority.LOW);
        assertTrue(todo.isOverdue());
        todo.markCompleted();
        assertFalse(todo.isOverdue());
        todo.setDueDate(LocalDate.now());
        assertTrue(todo.isDueToday());
    }

    @Test
    void testProjectTodoRelationship() {
        Project project = new Project("P", null, LocalDate.now(), LocalDate.now().plusDays(5));

        assertEquals(0, project.getTodoCount());
        assertEquals(0.0, project.getCompletionPercentage());
        assertFalse(project.isCompleted());
    }

    @Test
    void testProjectIterator() {
        Project project = new Project("P");
        Iterator<Todo> it = project.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    void testEqualsAndHashCode() {
        Todo t1 = new Todo("A");
        t1.setId(1L);
        Todo t2 = new Todo("A");
        t2.setId(1L);
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        Project p1 = new Project("P");
        p1.setId(2L);
        Project p2 = new Project("P");
        p2.setId(2L);
        assertEquals(p1, p2);
    }

    @Test
    void testToStringMethods() {
        User user = new User("lucian", "lucian@example.com");
        assertTrue(user.toString().contains("lucian"));
        Project project = new Project("P");
        assertTrue(project.toString().contains("P"));
        Todo todo = new Todo("T");
        assertTrue(todo.toString().contains("T"));
        RecurringTodo recurring = new RecurringTodo("R");
        assertTrue(recurring.toString().contains("RecurringTodo"));
    }
}
