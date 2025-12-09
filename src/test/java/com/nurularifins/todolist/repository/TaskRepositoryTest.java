package com.nurularifins.todolist.repository;

import com.nurularifins.todolist.entity.Task;
import com.nurularifins.todolist.enums.TaskPriority;
import com.nurularifins.todolist.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TaskRepository")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setTitle("Sample Task");
        sampleTask.setDescription("Sample Description");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setPriority(TaskPriority.MEDIUM);
    }

    @Nested
    @DisplayName("Save operations")
    class SaveOperations {

        @Test
        @DisplayName("should save task with generated UUID")
        void shouldSaveTask() {
            // When
            Task saved = taskRepository.save(sampleTask);

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getTitle()).isEqualTo("Sample Task");
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should update existing task")
        void shouldUpdateTask() {
            // Given
            Task saved = taskRepository.save(sampleTask);
            UUID id = saved.getId();
            entityManager.flush();
            entityManager.clear();

            // When
            Task toUpdate = taskRepository.findById(id).orElseThrow();
            toUpdate.setTitle("Updated Title");
            toUpdate.setStatus(TaskStatus.IN_PROGRESS);
            Task updated = taskRepository.save(toUpdate);

            // Then
            assertThat(updated.getTitle()).isEqualTo("Updated Title");
            assertThat(updated.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }
    }

    @Nested
    @DisplayName("Find operations")
    class FindOperations {

        @Test
        @DisplayName("should find task by ID")
        void shouldFindTaskById() {
            // Given
            Task saved = taskRepository.save(sampleTask);
            entityManager.flush();
            entityManager.clear();

            // When
            Optional<Task> found = taskRepository.findById(saved.getId());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Sample Task");
        }

        @Test
        @DisplayName("should return empty when task not found")
        void shouldReturnEmptyWhenNotFound() {
            // When
            Optional<Task> found = taskRepository.findById(UUID.randomUUID());

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("should find tasks by status")
        void shouldFindTasksByStatus() {
            // Given
            taskRepository.save(sampleTask);

            Task inProgressTask = new Task();
            inProgressTask.setTitle("In Progress Task");
            inProgressTask.setStatus(TaskStatus.IN_PROGRESS);
            inProgressTask.setPriority(TaskPriority.HIGH);
            taskRepository.save(inProgressTask);

            Task doneTask = new Task();
            doneTask.setTitle("Done Task");
            doneTask.setStatus(TaskStatus.DONE);
            doneTask.setPriority(TaskPriority.LOW);
            doneTask.setCompletedAt(LocalDateTime.now());
            taskRepository.save(doneTask);

            entityManager.flush();
            entityManager.clear();

            // When
            List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);
            List<Task> inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);
            List<Task> doneTasks = taskRepository.findByStatus(TaskStatus.DONE);

            // Then
            assertThat(todoTasks).hasSize(1);
            assertThat(inProgressTasks).hasSize(1);
            assertThat(doneTasks).hasSize(1);
        }

        @Test
        @DisplayName("should find tasks by priority")
        void shouldFindTasksByPriority() {
            // Given
            taskRepository.save(sampleTask); // MEDIUM

            Task urgentTask = new Task();
            urgentTask.setTitle("Urgent Task");
            urgentTask.setStatus(TaskStatus.TODO);
            urgentTask.setPriority(TaskPriority.URGENT);
            taskRepository.save(urgentTask);

            entityManager.flush();
            entityManager.clear();

            // When
            List<Task> mediumTasks = taskRepository.findByPriority(TaskPriority.MEDIUM);
            List<Task> urgentTasks = taskRepository.findByPriority(TaskPriority.URGENT);

            // Then
            assertThat(mediumTasks).hasSize(1);
            assertThat(urgentTasks).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Search operations")
    class SearchOperations {

        @Test
        @DisplayName("should search by title containing keyword")
        void shouldSearchByTitleOrDescription() {
            // Given
            sampleTask.setTitle("Important meeting notes");
            sampleTask.setDescription("Discuss project timeline");
            taskRepository.save(sampleTask);

            Task anotherTask = new Task();
            anotherTask.setTitle("Shopping list");
            anotherTask.setDescription("Buy important items");
            anotherTask.setStatus(TaskStatus.TODO);
            anotherTask.setPriority(TaskPriority.LOW);
            taskRepository.save(anotherTask);

            entityManager.flush();
            entityManager.clear();

            // When
            List<Task> resultTitle = taskRepository.searchByTitleOrDescription("meeting");
            List<Task> resultDesc = taskRepository.searchByTitleOrDescription("important");

            // Then
            assertThat(resultTitle).hasSize(1);
            assertThat(resultTitle.get(0).getTitle()).contains("meeting");

            assertThat(resultDesc).hasSize(2); // Both tasks have "important"
        }
    }

    @Nested
    @DisplayName("Pagination")
    class PaginationTests {

        @Test
        @DisplayName("should paginate results")
        void shouldPaginateResults() {
            // Given: Create 25 tasks
            for (int i = 1; i <= 25; i++) {
                Task task = new Task();
                task.setTitle("Task " + i);
                task.setStatus(TaskStatus.TODO);
                task.setPriority(TaskPriority.MEDIUM);
                taskRepository.save(task);
            }
            entityManager.flush();
            entityManager.clear();

            // When
            Page<Task> firstPage = taskRepository.findAll(PageRequest.of(0, 10));
            Page<Task> secondPage = taskRepository.findAll(PageRequest.of(1, 10));
            Page<Task> thirdPage = taskRepository.findAll(PageRequest.of(2, 10));

            // Then
            assertThat(firstPage.getContent()).hasSize(10);
            assertThat(secondPage.getContent()).hasSize(10);
            assertThat(thirdPage.getContent()).hasSize(5);
            assertThat(firstPage.getTotalElements()).isEqualTo(25);
            assertThat(firstPage.getTotalPages()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Archive operations")
    class ArchiveOperations {

        @Test
        @DisplayName("should find only non-archived tasks")
        void shouldFindNonArchivedTasks() {
            // Given
            taskRepository.save(sampleTask);

            Task archivedTask = new Task();
            archivedTask.setTitle("Archived Task");
            archivedTask.setStatus(TaskStatus.DONE);
            archivedTask.setPriority(TaskPriority.LOW);
            archivedTask.setArchived(true);
            taskRepository.save(archivedTask);

            entityManager.flush();
            entityManager.clear();

            // When
            List<Task> activeTasks = taskRepository.findByArchivedFalse();

            // Then
            assertThat(activeTasks).hasSize(1);
            assertThat(activeTasks.get(0).getTitle()).isEqualTo("Sample Task");
        }
    }

    @Nested
    @DisplayName("Due date operations")
    class DueDateOperations {

        @Test
        @DisplayName("should find tasks with due date before given date")
        void shouldFindTasksDueBefore() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            Task overdue = new Task();
            overdue.setTitle("Overdue Task");
            overdue.setStatus(TaskStatus.TODO);
            overdue.setPriority(TaskPriority.HIGH);
            overdue.setDueDate(now.minusDays(1));
            taskRepository.save(overdue);

            Task future = new Task();
            future.setTitle("Future Task");
            future.setStatus(TaskStatus.TODO);
            future.setPriority(TaskPriority.LOW);
            future.setDueDate(now.plusDays(7));
            taskRepository.save(future);

            entityManager.flush();
            entityManager.clear();

            // When
            List<Task> overdueTasks = taskRepository.findByDueDateBeforeAndStatusNot(now, TaskStatus.DONE);

            // Then
            assertThat(overdueTasks).hasSize(1);
            assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Overdue Task");
        }
    }
}
