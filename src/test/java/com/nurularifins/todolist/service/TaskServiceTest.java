package com.nurularifins.todolist.service;

import com.nurularifins.todolist.dto.TaskDto;
import com.nurularifins.todolist.entity.Task;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.exception.InvalidTaskException;
import com.nurularifins.todolist.exception.TaskNotFoundException;
import com.nurularifins.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskDto validTaskDto;
    private Task savedTask;

    @BeforeEach
    void setUp() {
        validTaskDto = new TaskDto();
        validTaskDto.setTitle("Test Task");
        validTaskDto.setDescription("Test Description");
        validTaskDto.setPriority(TaskPriority.MEDIUM);

        savedTask = new Task();
        savedTask.setId(UUID.randomUUID());
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setStatus(TaskStatus.TODO);
        savedTask.setPriority(TaskPriority.MEDIUM);
        savedTask.setCreatedAt(LocalDateTime.now());
        savedTask.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create Task")
    class CreateTask {

        @Test
        @DisplayName("should create task successfully")
        void shouldCreateTaskSuccessfully() {
            // Given
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            // When
            TaskDto result = taskService.createTask(validTaskDto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(savedTask.getId());
            assertThat(result.getTitle()).isEqualTo("Test Task");
            assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("should throw exception when title is null")
        void shouldThrowExceptionWhenTitleNull() {
            // Given
            validTaskDto.setTitle(null);

            // When/Then
            assertThatThrownBy(() -> taskService.createTask(validTaskDto))
                    .isInstanceOf(InvalidTaskException.class)
                    .hasMessageContaining("title");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when title is blank")
        void shouldThrowExceptionWhenTitleBlank() {
            // Given
            validTaskDto.setTitle("   ");

            // When/Then
            assertThatThrownBy(() -> taskService.createTask(validTaskDto))
                    .isInstanceOf(InvalidTaskException.class)
                    .hasMessageContaining("title");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when due date is in the past")
        void shouldThrowExceptionWhenDueDateInPast() {
            // Given
            validTaskDto.setDueDate(LocalDateTime.now().minusDays(1));

            // When/Then
            assertThatThrownBy(() -> taskService.createTask(validTaskDto))
                    .isInstanceOf(InvalidTaskException.class)
                    .hasMessageContaining("due date");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create task with default status TODO")
        void shouldCreateTaskWithDefaultStatusTodo() {
            // Given
            validTaskDto.setStatus(null);
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            // When
            TaskDto result = taskService.createTask(validTaskDto);

            // Then
            assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        }

        @Test
        @DisplayName("should create task with default priority MEDIUM")
        void shouldCreateTaskWithDefaultPriorityMedium() {
            // Given
            validTaskDto.setPriority(null);
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            // When
            TaskDto result = taskService.createTask(validTaskDto);

            // Then
            assertThat(result.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        }
    }

    @Nested
    @DisplayName("Update Task")
    class UpdateTask {

        @Test
        @DisplayName("should update task successfully")
        void shouldUpdateTaskSuccessfully() {
            // Given
            UUID taskId = savedTask.getId();
            TaskDto updateDto = new TaskDto();
            updateDto.setTitle("Updated Title");
            updateDto.setDescription("Updated Description");
            updateDto.setStatus(TaskStatus.IN_PROGRESS);
            updateDto.setPriority(TaskPriority.HIGH);

            Task updatedTask = new Task();
            updatedTask.setId(taskId);
            updatedTask.setTitle("Updated Title");
            updatedTask.setDescription("Updated Description");
            updatedTask.setStatus(TaskStatus.IN_PROGRESS);
            updatedTask.setPriority(TaskPriority.HIGH);
            updatedTask.setCreatedAt(savedTask.getCreatedAt());
            updatedTask.setUpdatedAt(LocalDateTime.now());

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(savedTask));
            when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

            // When
            TaskDto result = taskService.updateTask(taskId, updateDto);

            // Then
            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("should throw exception when task not found")
        void shouldThrowExceptionWhenTaskNotFound() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> taskService.updateTask(nonExistentId, validTaskDto))
                    .isInstanceOf(TaskNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete Task")
    class DeleteTask {

        @Test
        @DisplayName("should soft delete task (archive)")
        void shouldSoftDeleteTask() {
            // Given
            UUID taskId = savedTask.getId();
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(savedTask));
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            // When
            taskService.deleteTask(taskId);

            // Then
            verify(taskRepository).save(any(Task.class));
            assertThat(savedTask.isArchived()).isTrue();
        }

        @Test
        @DisplayName("should throw exception when deleting non-existent task")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> taskService.deleteTask(nonExistentId))
                    .isInstanceOf(TaskNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Mark Task as Complete")
    class MarkTaskComplete {

        @Test
        @DisplayName("should mark task as complete")
        void shouldMarkTaskAsComplete() {
            // Given
            UUID taskId = savedTask.getId();
            Task completedTask = new Task();
            completedTask.setId(taskId);
            completedTask.setTitle(savedTask.getTitle());
            completedTask.setStatus(TaskStatus.DONE);
            completedTask.setCompletedAt(LocalDateTime.now());
            completedTask.setPriority(TaskPriority.MEDIUM);
            completedTask.setCreatedAt(savedTask.getCreatedAt());
            completedTask.setUpdatedAt(LocalDateTime.now());

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(savedTask));
            when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

            // When
            TaskDto result = taskService.markAsComplete(taskId);

            // Then
            assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
            assertThat(result.getCompletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Tasks")
    class GetTasks {

        @Test
        @DisplayName("should get all tasks paginated")
        void shouldGetAllTasksPaginated() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Task> taskPage = new PageImpl<>(List.of(savedTask), pageable, 1);
            when(taskRepository.findByArchivedFalse()).thenReturn(List.of(savedTask));

            // When
            List<TaskDto> result = taskService.getAllTasks();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        }

        @Test
        @DisplayName("should get task by ID")
        void shouldGetTaskById() {
            // Given
            UUID taskId = savedTask.getId();
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(savedTask));

            // When
            TaskDto result = taskService.getTaskById(taskId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(taskId);
        }

        @Test
        @DisplayName("should throw exception when task not found by ID")
        void shouldThrowExceptionWhenNotFoundById() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> taskService.getTaskById(nonExistentId))
                    .isInstanceOf(TaskNotFoundException.class);
        }

        @Test
        @DisplayName("should filter tasks by status")
        void shouldFilterTasksByStatus() {
            // Given
            when(taskRepository.findByStatusAndArchivedFalse(TaskStatus.TODO))
                    .thenReturn(List.of(savedTask));

            // When
            List<TaskDto> result = taskService.getTasksByStatus(TaskStatus.TODO);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(TaskStatus.TODO);
        }

        @Test
        @DisplayName("should search tasks by keyword")
        void shouldSearchTasksByKeyword() {
            // Given
            when(taskRepository.searchByTitleOrDescription("Test"))
                    .thenReturn(List.of(savedTask));

            // When
            List<TaskDto> result = taskService.searchTasks("Test");

            // Then
            assertThat(result).hasSize(1);
        }
    }
}
