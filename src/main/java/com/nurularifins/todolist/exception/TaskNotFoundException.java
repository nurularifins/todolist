package com.nurularifins.todolist.exception;

import java.util.UUID;

/**
 * Exception thrown when a task is not found.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID id) {
        super("Task not found with id: " + id);
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
