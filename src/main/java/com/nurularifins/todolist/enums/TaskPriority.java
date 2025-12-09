package com.nurularifins.todolist.enums;

/**
 * Represents the priority level of a task.
 */
public enum TaskPriority {
    LOW("Low", "#10B981"),      // Green
    MEDIUM("Medium", "#F59E0B"), // Yellow
    HIGH("High", "#F97316"),     // Orange
    URGENT("Urgent", "#EF4444"); // Red

    private final String displayName;
    private final String color;

    TaskPriority(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}
