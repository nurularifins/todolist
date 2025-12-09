# Claude Instructions

## Project Overview

TodoList adalah aplikasi web-based task management. Dibangun dengan Spring Boot 4.0 + Thymeleaf + MySQL.

**Status:** Phase 1 Complete - Next: Phase 2 (User Management)

## Core Principles

1. **TDD First** - Tulis test SEBELUM implementasi. Tidak ada code tanpa test.
2. **Documentation as Spec** - Dokumentasi adalah spesifikasi. Ikuti ADR.
3. **Simple over Clever** - Kode sederhana lebih baik dari kode pintar.
4. **Security by Default** - Validasi ownership, sanitize input, hash password.

## TDD Workflow (WAJIB)

```
Untuk SETIAP fitur yang diimplementasi:

1. RED   → Tulis test yang FAIL (based on acceptance criteria)
2. GREEN → Tulis kode MINIMUM untuk pass test
3. REFACTOR → Clean up, tests tetap pass
4. COMMIT → "test: ...", "feat: ...", "refactor: ..."
```

**Coverage Requirements:**
- Service Layer: 100%
- Repository Layer: 80%
- Controller Layer: 90%
- Overall: minimum 70%

**Code Analysis:** SonarCloud (auto-scan on push)

## Quick Reference

| Topik | Dokumen |
|-------|---------|
| Features & Scope | [`docs/01-product-spec.md`](docs/01-product-spec.md) |
| Architecture & Tech Stack | [`docs/02-architecture.md`](docs/02-architecture.md) |
| Implementation Plan (TDD) | [`docs/03-implementation-plan.md`](docs/03-implementation-plan.md) |
| Database Schema | [`docs/adr/002-database-design.md`](docs/adr/002-database-design.md) |
| Security Details | [`docs/adr/003-authentication.md`](docs/adr/003-authentication.md) |

## Development Rules

### Code Quality
- Constructor injection (bukan @Autowired field)
- DTOs untuk request/response (jangan expose entities)
- Validation annotations (@Valid, @NotNull, @Size)
- Meaningful names (no single letters kecuali loop counters)
- Javadoc untuk public methods

### Database
- Flyway untuk migrations: `V{version}__{description}.sql`
- SEMUA migrations HARUS reversible
- JANGAN edit migration yang sudah applied

### Error Handling
- RAISE exception jika data invalid (jangan diam-diam pakai default)
- Custom exceptions: TaskNotFoundException, UnauthorizedAccessException
- Global exception handler dengan @ControllerAdvice

### Security
- SELALU validate user ownership sebelum access/modify
- @PreAuthorize untuk method-level security
- CSRF protection enabled
- BCrypt password hashing (strength 10)
- JANGAN log password atau sensitive data

## Forbidden Practices

- **NO Lombok** - Prefer plain Java untuk clarity
- **NO commit secrets** - .env, passwords, API keys
- **NO bypass security** - No permitAll untuk authenticated endpoints
- **NO hardcode config** - Use application.yml
- **NO God classes** - Single responsibility principle

## Out of Scope (JANGAN Implement)

- Recurring tasks, Subtasks, File attachments
- Calendar view, Kanban board, Time tracking
- Custom fields, Mobile app (native)
- Real-time collaboration (WebSocket)
- Third-party integrations, AI features

## Common Commands

```bash
# Run app
./mvnw spring-boot:run

# Run tests (WAJIB sebelum commit)
./mvnw test

# Run with coverage
./mvnw clean verify

# Coverage report
open target/site/jacoco/index.html

# Docker
docker-compose up -d
```

## Project Structure

```
src/main/java/com/nurularifins/todolist/
├── config/          # Spring configuration
├── controller/      # MVC controllers
├── service/         # Business logic
├── repository/      # Spring Data JPA
├── entity/          # JPA entities
├── dto/             # Data Transfer Objects
├── enums/           # TaskStatus, Priority, etc.
├── security/        # Security services
├── exception/       # Custom exceptions
└── scheduler/       # Scheduled jobs

src/test/java/       # MIRROR structure untuk tests
```

## Coding Conventions

### Test Example (Tulis INI dulu!)

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
        TaskDto dto = TaskDto.builder()
            .title("Test Task")
            .build();
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
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

### Service Implementation (Tulis SETELAH test pass)

```java
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDto createTask(TaskDto dto, User currentUser) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setUser(currentUser);
        task.setStatus(TaskStatus.TODO);

        Task saved = taskRepository.save(task);
        return TaskDto.fromEntity(saved);
    }
}
```

## Prompt Tips

### Good Prompts

```
"Implementasikan TaskService.createTask() dengan TDD.
Acceptance criteria dari docs/03-implementation-plan.md Phase 1."

"Buatkan test untuk TaskRepository.findByUserAndStatus()
berdasarkan schema di ADR-002."
```

### Bad Prompts

```
"Buatkan aplikasi todolist" → Terlalu vague

"Tambahkan fitur calendar view" → Out of scope!

"Implement tanpa test dulu" → TIDAK BOLEH!
```

## Remember

**Test First. Always.**
