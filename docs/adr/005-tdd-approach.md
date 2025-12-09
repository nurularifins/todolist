# ADR-005: Test-Driven Development (TDD)

## Status

Accepted

## Context

TodoList App menggunakan TDD sebagai core development methodology. Test bukan afterthought, tapi **spesifikasi executable** dari behavior yang diinginkan.

## Decision

### TDD Workflow (Red-Green-Refactor)

```
1. RED    → Tulis test yang FAIL (berdasarkan acceptance criteria)
2. GREEN  → Implementasi MINIMUM untuk pass test
3. REFACTOR → Clean up code (tests tetap pass)
4. COMMIT
```

### Test Pyramid

```
        ┌─────────────┐
        │    E2E      │  10% - Critical paths only
        └─────────────┘
      ┌─────────────────┐
      │  Integration    │  20% - Database, email
      └─────────────────┘
    ┌───────────────────────┐
    │    Unit Tests         │  70% - Business logic
    └───────────────────────┘
```

### Coverage Requirements

| Layer | Minimum | Target |
|-------|---------|--------|
| Service | 90% | 100% |
| Repository | 70% | 90% |
| Controller | 80% | 95% |
| Overall | **70%** | 85% |

## Test Examples

### Unit Test (Service Layer)

```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Should create task when data valid")
    void shouldCreateTaskWhenDataValid() {
        // Given
        TaskDto dto = TaskDto.builder().title("Test").build();
        when(taskRepository.save(any())).thenReturn(new Task());

        // When
        TaskDto result = taskService.createTask(dto, user);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when title null")
    void shouldThrowExceptionWhenTitleNull() {
        TaskDto dto = TaskDto.builder().title(null).build();

        assertThatThrownBy(() -> taskService.createTask(dto, user))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Title is required");
    }
}
```

### Integration Test (Repository Layer)

```java
@DataJpaTest
@Testcontainers
class TaskRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withReuse(true);

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Should find tasks by status")
    void shouldFindTasksByStatus() {
        // Given
        taskRepository.saveAll(List.of(
            createTask("Task 1", TaskStatus.TODO),
            createTask("Task 2", TaskStatus.DONE)
        ));

        // When
        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);

        // Then
        assertThat(todoTasks).hasSize(1);
        assertThat(todoTasks.get(0).getTitle()).isEqualTo("Task 1");
    }
}
```

### Controller Test (MockMvc)

```java
@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @WithMockUser
    @DisplayName("Should create task and redirect")
    void shouldCreateTask() throws Exception {
        TaskDto created = TaskDto.builder().id(UUID.randomUUID()).build();
        when(taskService.createTask(any(), any())).thenReturn(created);

        mockMvc.perform(post("/tasks")
                .param("title", "Test")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/tasks/" + created.getId()));
    }

    @Test
    @DisplayName("Should require authentication")
    void shouldRequireAuth() throws Exception {
        mockMvc.perform(post("/tasks").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should enforce CSRF")
    void shouldEnforceCSRF() throws Exception {
        mockMvc.perform(post("/tasks").param("title", "Test"))
            .andExpect(status().isForbidden());
    }
}
```

## Test Naming Convention

```java
// Pattern: should[ExpectedBehavior]When[Condition]

// Good
void shouldCreateTaskWhenDataValid() { }
void shouldThrowExceptionWhenTitleNull() { }
void shouldReturnEmptyListWhenNoTasksFound() { }

// Bad
void testTask() { }  // Too vague
void test1() { }     // No meaning
```

## Test Data Builder

```java
public class TaskTestBuilder {
    private String title = "Default Task";
    private TaskStatus status = TaskStatus.TODO;
    private TaskPriority priority = TaskPriority.MEDIUM;

    public static TaskTestBuilder aTask() {
        return new TaskTestBuilder();
    }

    public TaskTestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TaskTestBuilder urgent() {
        this.priority = TaskPriority.URGENT;
        return this;
    }

    public Task build() {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setPriority(priority);
        return task;
    }
}

// Usage
Task task = aTask().withTitle("Important").urgent().build();
```

## Tools

| Tool | Purpose |
|------|---------|
| JUnit 5 | Test framework |
| Mockito | Mocking |
| AssertJ | Fluent assertions |
| Testcontainers | Real MySQL for integration tests |
| JaCoCo | Coverage reporting |
| SonarCloud | Code quality analysis |
| MockMvc | Controller testing |

## Maven Configuration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>jacoco-check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Commands

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=TaskServiceTest

# Run with coverage
./mvnw clean verify

# View coverage report
open target/site/jacoco/index.html
```

## Best Practices

### DO
- Write test BEFORE implementation
- One assertion per test (ideal)
- Use descriptive test names
- Mock external dependencies
- Use Testcontainers for DB tests

### DON'T
- Test getters/setters
- Test framework code
- Write brittle tests
- Skip tests to "save time"
- Change tests to make them pass

## Reference

- Implementation Plan: [`../03-implementation-plan.md`](../03-implementation-plan.md)
