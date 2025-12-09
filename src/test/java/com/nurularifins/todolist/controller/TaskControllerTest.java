package com.nurularifins.todolist.controller;

import com.nurularifins.todolist.dto.TaskDto;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import com.nurularifins.todolist.exception.TaskNotFoundException;
import com.nurularifins.todolist.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = TaskController.class, properties = "spring.thymeleaf.prefix=classpath:/templates-test/")
@DisplayName("TaskController")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private TaskDto sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new TaskDto();
        sampleTask.setId(UUID.randomUUID());
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Test Description");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setPriority(TaskPriority.MEDIUM);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Task List")
    class TaskList {

        @Test
        @WithMockUser
        @DisplayName("should display task list page")
        void shouldDisplayTaskListPage() throws Exception {
            // Given
            when(taskService.getAllTasks()).thenReturn(List.of(sampleTask));

            // When/Then
            mockMvc.perform(get("/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/list"))
                    .andExpect(model().attributeExists("tasks"));
        }

        @Test
        @WithMockUser
        @DisplayName("should filter tasks by status")
        void shouldFilterTasksByStatus() throws Exception {
            // Given
            when(taskService.getTasksByStatus(TaskStatus.TODO)).thenReturn(List.of(sampleTask));

            // When/Then
            mockMvc.perform(get("/tasks").param("status", "TODO"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/list"));

            verify(taskService).getTasksByStatus(TaskStatus.TODO);
        }

        @Test
        @WithMockUser
        @DisplayName("should search tasks by keyword")
        void shouldSearchTasksByKeyword() throws Exception {
            // Given
            when(taskService.searchTasks("test")).thenReturn(List.of(sampleTask));

            // When/Then
            mockMvc.perform(get("/tasks").param("search", "test"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/list"));

            verify(taskService).searchTasks("test");
        }
    }

    @Nested
    @DisplayName("Task Detail")
    class TaskDetail {

        @Test
        @WithMockUser
        @DisplayName("should display task detail page")
        void shouldDisplayTaskDetailPage() throws Exception {
            // Given
            UUID taskId = sampleTask.getId();
            when(taskService.getTaskById(taskId)).thenReturn(sampleTask);

            // When/Then
            mockMvc.perform(get("/tasks/{id}", taskId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/detail"))
                    .andExpect(model().attributeExists("task"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return 404 when task not found")
        void shouldReturn404WhenTaskNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(taskService.getTaskById(nonExistentId))
                    .thenThrow(new TaskNotFoundException(nonExistentId));

            // When/Then
            mockMvc.perform(get("/tasks/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Create Task")
    class CreateTask {

        @Test
        @WithMockUser
        @DisplayName("should display create task form")
        void shouldDisplayCreateTaskForm() throws Exception {
            mockMvc.perform(get("/tasks/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/form"))
                    .andExpect(model().attributeExists("task"))
                    .andExpect(model().attributeExists("statuses"))
                    .andExpect(model().attributeExists("priorities"));
        }

        @Test
        @WithMockUser
        @DisplayName("should create task successfully")
        void shouldCreateTaskSuccessfully() throws Exception {
            // Given
            when(taskService.createTask(any(TaskDto.class))).thenReturn(sampleTask);

            // When/Then
            mockMvc.perform(post("/tasks")
                            .with(csrf())
                            .param("title", "New Task")
                            .param("description", "New Description")
                            .param("priority", "MEDIUM"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/tasks"))
                    .andExpect(flash().attributeExists("success"));
        }

        @Test
        @WithMockUser
        @DisplayName("should return validation error when title is empty")
        void shouldReturnValidationErrorWhenTitleEmpty() throws Exception {
            mockMvc.perform(post("/tasks")
                            .with(csrf())
                            .param("title", "")
                            .param("description", "Description"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/form"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("should require authentication for create")
        void shouldRequireAuthenticationForCreate() throws Exception {
            mockMvc.perform(post("/tasks")
                            .with(csrf())
                            .param("title", "New Task"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should require CSRF token")
        void shouldRequireCSRFToken() throws Exception {
            mockMvc.perform(post("/tasks")
                            .param("title", "New Task"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Update Task")
    class UpdateTask {

        @Test
        @WithMockUser
        @DisplayName("should display edit task form")
        void shouldDisplayEditTaskForm() throws Exception {
            // Given
            UUID taskId = sampleTask.getId();
            when(taskService.getTaskById(taskId)).thenReturn(sampleTask);

            // When/Then
            mockMvc.perform(get("/tasks/{id}/edit", taskId))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/form"))
                    .andExpect(model().attributeExists("task"));
        }

        @Test
        @WithMockUser
        @DisplayName("should update task successfully")
        void shouldUpdateTaskSuccessfully() throws Exception {
            // Given
            UUID taskId = sampleTask.getId();
            when(taskService.updateTask(any(UUID.class), any(TaskDto.class))).thenReturn(sampleTask);

            // When/Then
            mockMvc.perform(post("/tasks/{id}", taskId)
                            .with(csrf())
                            .param("title", "Updated Task")
                            .param("description", "Updated Description")
                            .param("status", "IN_PROGRESS")
                            .param("priority", "HIGH"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/tasks/" + taskId))
                    .andExpect(flash().attributeExists("success"));
        }
    }

    @Nested
    @DisplayName("Delete Task")
    class DeleteTask {

        @Test
        @WithMockUser
        @DisplayName("should delete task successfully")
        void shouldDeleteTaskSuccessfully() throws Exception {
            // Given
            UUID taskId = sampleTask.getId();

            // When/Then
            mockMvc.perform(post("/tasks/{id}/delete", taskId)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/tasks"))
                    .andExpect(flash().attributeExists("success"));

            verify(taskService).deleteTask(taskId);
        }
    }

    @Nested
    @DisplayName("Mark Complete")
    class MarkComplete {

        @Test
        @WithMockUser
        @DisplayName("should mark task as complete")
        void shouldMarkTaskAsComplete() throws Exception {
            // Given
            UUID taskId = sampleTask.getId();
            TaskDto completedTask = new TaskDto();
            completedTask.setId(taskId);
            completedTask.setStatus(TaskStatus.DONE);
            when(taskService.markAsComplete(taskId)).thenReturn(completedTask);

            // When/Then
            mockMvc.perform(post("/tasks/{id}/complete", taskId)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/tasks/" + taskId));

            verify(taskService).markAsComplete(taskId);
        }
    }
}
