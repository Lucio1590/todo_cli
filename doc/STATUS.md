# Project Status: Todo Management System

## Implemented Features

### Core Domain Model
- `Todo` class with fields: id, title, description, due date, priority, status, projectId, userId, createdAt, updatedAt.
- Enum types for:
  - `Priority` (LOW, MEDIUM, HIGH, URGENT)
  - `TodoStatus` (TODO, IN_PROGRESS, COMPLETED, CANCELLED) with utility methods (e.g., `isFinished`, `isModifiable`, etc.)
  - `TodoType` (SIMPLE, RECURRING)

### Exception Handling
- Custom exception: `NotImplementedException` for marking unimplemented features.

### Application Entry Point
- Main class: [`org.lucian.todos.Main`](src/main/java/org/lucian/todos/Main.java)
  - Logs application startup and shutdown.
  - Prints a welcome message.
  - Throws `NotImplementedException` as a placeholder for future CLI logic.

### Logging
- SLF4J + Logback configuration:
  - Console and rolling file appenders.
  - Root logger at INFO level.
  - (Note: Application-specific logger currently references an unused package, see TODOs.)

### Testing
- JUnit 5 and Mockito setup.
- Basic tests for:
  - JUnit setup verification.
  - Application name utility method.

## Not Yet Implemented

- CLI interface and user interaction logic.
- Database integration (SQLite dependency is present for future use).
- Project and user management.
- Advanced features (recurring todos, etc.).

## TODOs / Known Issues

- Update Logback logger configuration to match the actual application package (`org.lucian.todos`).
- Implement main CLI logic in `Main`.
- Add more comprehensive unit and integration tests.

---

_Last updated: 29/05/2025