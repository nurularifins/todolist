package com.nurularifins.todolist.service;

import com.nurularifins.todolist.dto.TaskDto;
import com.nurularifins.todolist.entity.Task;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.exception.InvalidTaskException;
import com.nurularifins.todolist.exception.TaskNotFoundException;
import com.nurularifins.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Task operations.
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Create a new task.
     */
    public TaskDto createTask(TaskDto dto) {
        validateTaskDto(dto);

        Task task = dto.toEntity();
        Task saved = taskRepository.save(task);
        return TaskDto.fromEntity(saved);
    }

    /**
     * Update an existing task.
     */
    public TaskDto updateTask(UUID id, TaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
            if (dto.getStatus() == TaskStatus.DONE && task.getCompletedAt() == null) {
                task.setCompletedAt(LocalDateTime.now());
            }
        }
        if (dto.getPriority() != null) {
            task.setPriority(dto.getPriority());
        }
        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate());
        }

        Task updated = taskRepository.save(task);
        return TaskDto.fromEntity(updated);
    }

    /**
     * Soft delete a task (archive it).
     */
    public void deleteTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setArchived(true);
        taskRepository.save(task);
    }

    /**
     * Mark a task as complete.
     */
    public TaskDto markAsComplete(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setStatus(TaskStatus.DONE);
        task.setCompletedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        return TaskDto.fromEntity(saved);
    }

    /**
     * Get all non-archived tasks.
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        return taskRepository.findByArchivedFalse()
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get task by ID.
     */
    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskDto.fromEntity(task);
    }

    /**
     * Get tasks by status.
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatusAndArchivedFalse(status)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by priority.
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriorityAndArchivedFalse(priority)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Search tasks by keyword in title or description.
     */
    @Transactional(readOnly = true)
    public List<TaskDto> searchTasks(String keyword) {
        return taskRepository.searchByTitleOrDescription(keyword)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Validate task DTO before creation.
     */
    private void validateTaskDto(TaskDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new InvalidTaskException("Task title is required");
        }

        if (dto.getDueDate() != null && dto.getDueDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTaskException("Task due date cannot be in the past");
        }
    }
}
