package com.nurularifins.todolist.repository;

import com.nurularifins.todolist.entity.Task;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Task entity operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    /**
     * Find all tasks with a specific status.
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find all tasks with a specific priority.
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Find all non-archived tasks.
     */
    List<Task> findByArchivedFalse();

    /**
     * Search tasks by title or description containing the keyword (case-insensitive).
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByTitleOrDescription(@Param("keyword") String keyword);

    /**
     * Find tasks with due date before given date and not completed.
     */
    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime dueDate, TaskStatus status);

    /**
     * Find all tasks by status that are not archived.
     */
    List<Task> findByStatusAndArchivedFalse(TaskStatus status);

    /**
     * Find all tasks by priority that are not archived.
     */
    List<Task> findByPriorityAndArchivedFalse(TaskPriority priority);
}
