package org.lucian.todos.dao.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.lucian.todos.dao.TodoDAO;
import org.lucian.todos.exceptions.DatabaseException;
import org.lucian.todos.exceptions.NotImplementedException;
import org.lucian.todos.model.Priority;
import org.lucian.todos.model.Todo;
import org.lucian.todos.model.TodoStatus;

public class TodoDAOImpl implements TodoDAO{

    @Override
    public Todo create(Todo todo) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Todo> findById(Long id) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<Todo> findAll() throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<Todo> findByProjectId(Long projectId) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<Todo> findByStatus(TodoStatus status) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<Todo> findByPriority(Priority priority) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<Todo> findDueBefore(LocalDate date) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<Todo> findOverdue() throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public Todo update(Todo todo) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public boolean delete(Long id) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public long count() throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public long countByStatus(TodoStatus status) throws DatabaseException {
        throw new NotImplementedException();
    }
    
}
