package com.nurularifins.todolist.service;

import com.nurularifins.todolist.dto.TaskDto;
import com.nurularifins.todolist.entity.Task;
import com.nurularifins.todolist.entity.User;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.exception.InvalidTaskException;
import com.nurularifins.todolist.exception.TaskNotFoundException;
import com.nurularifins.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("user@example.com");

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setEmail("other@example.com");
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() {
        // Given
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("New Task");
        taskDto.setDescription("Description");
        taskDto.setPriority(TaskPriority.MEDIUM);

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID());
        savedTask.setTitle(taskDto.getTitle());
        savedTask.setUser(testUser);
        savedTask.setPriority(TaskPriority.MEDIUM);
        savedTask.setStatus(TaskStatus.TODO);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        TaskDto created = taskService.createTask(taskDto, testUser);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getTitle()).isEqualTo("New Task");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when creating task with null title")
    void shouldThrowExceptionWhenTitleNull() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle(null);

        assertThatThrownBy(() -> taskService.createTask(taskDto, testUser))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessage("Task title is required");
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Title");
        existingTask.setUser(testUser); // Owned by testUser

        TaskDto updateDto = new TaskDto();
        updateDto.setTitle("New Title");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // When
        TaskDto updated = taskService.updateTask(taskId, updateDto, testUser);

        // Then
        assertThat(updated.getTitle()).isEqualTo("New Title");
        verify(taskRepository).save(existingTask);
    }

    @Test
    @DisplayName("Should throw not found exception when updating task belonging to another user")
    void shouldThrowExceptionWhenUpdatingOtherUserTask() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Other User Task");
        existingTask.setUser(otherUser); // Owned by otherUser

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // When/Then
        assertThatThrownBy(() -> taskService.updateTask(taskId, new TaskDto(), testUser))
                .isInstanceOf(TaskNotFoundException.class); // Should be 404 for security
    }

    @Test
    @DisplayName("Should soft delete task successfully")
    void shouldDeleteTask() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setUser(testUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // When
        taskService.deleteTask(taskId, testUser);

        // Then
        assertThat(task.isArchived()).isTrue();
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("Should throw not found exception when deleting task of another user")
    void shouldThrowExceptionWhenDeletingOtherUserTask() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setUser(otherUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When/Then
        assertThatThrownBy(() -> taskService.deleteTask(taskId, testUser))
                .isInstanceOf(TaskNotFoundException.class);

        assertThat(task.isArchived()).isFalse();
        verify(taskRepository, never()).save(task);
    }

    @Test
    @DisplayName("Should mark task as complete")
    void shouldMarkTaskAsComplete() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.TODO);
        task.setUser(testUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // When
        TaskDto completed = taskService.markAsComplete(taskId, testUser);

        // Then
        assertThat(completed.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(completed.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should get all tasks for user")
    void shouldGetAllTasks() {
        // Given
        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTitle("Task 1");
        task.setUser(testUser);

        when(taskRepository.findByUserAndArchivedFalse(testUser)).thenReturn(List.of(task));

        // When
        List<TaskDto> tasks = taskService.getAllTasks(testUser);

        // Then
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    @DisplayName("Should get task by id for user")
    void shouldGetTaskById() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("My Task");
        task.setUser(testUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When
        TaskDto found = taskService.getTaskById(taskId, testUser);

        // Then
        assertThat(found.getTitle()).isEqualTo("My Task");
    }

    @Test
    @DisplayName("Should throw exception when getting task of another user")
    void shouldThrowExceptionWhenGetTaskOfOtherUser() {
        // Given
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);
        task.setUser(otherUser);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // When/Then
        assertThatThrownBy(() -> taskService.getTaskById(taskId, testUser))
                .isInstanceOf(TaskNotFoundException.class);
    }
}
