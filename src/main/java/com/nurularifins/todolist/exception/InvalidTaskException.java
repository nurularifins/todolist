package com.nurularifins.todolist.exception;

/**
 * Exception thrown when task data is invalid.
 */
public class InvalidTaskException extends RuntimeException {

    public InvalidTaskException(String message) {
        super(message);
    }
}
