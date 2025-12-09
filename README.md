# TodoList App

Web-based task management application built with Spring Boot 3.4 + Thymeleaf + MySQL.

**Status:** Phase 1 - Basic Task Management (Complete)

## Features (Current)

- Task CRUD (Create, Read, Update, Delete)
- Task filtering by status and priority
- Task search by title/description
- Soft delete (archive) tasks
- Mark tasks as complete
- Responsive UI with Tailwind CSS

## Screenshots

| Task List | Task Detail | Create Task |
|-----------|-------------|-------------|
| Filter & search tasks | View task details | Create new task |

## Quick Start

### Prerequisites
- Java 21
- MySQL 8.0 (or Docker)
- Maven 3.9+

### 1. Start Database

**Option A: Docker (recommended)**
```bash
docker-compose up -d
```

**Option B: Local MySQL**
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE todolist_db;"
```

### 2. Configure Environment

```bash
cp .env.example .env
# Edit .env with your database credentials
```

### 3. Run Application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Open Browser

```
http://localhost:8080
```

**Default credentials:** `user` / (check console for generated password)

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.4.1, Java 21 |
| Frontend | Thymeleaf, Tailwind CSS |
| Database | MySQL 8.0, Flyway migrations |
| Security | Spring Security 6 (session-based) |
| Testing | JUnit 5, Mockito, H2 (test) |
| Coverage | JaCoCo (69.7% overall, 88.6% critical paths) |
| Build | Maven |
| Container | Docker + Docker Compose |

## Project Structure

```
src/main/java/com/nurularifins/todolist/
├── config/          # Spring configuration (SecurityConfig)
├── controller/      # MVC controllers (TaskController)
├── service/         # Business logic (TaskService)
├── repository/      # Spring Data JPA repositories
├── entity/          # JPA entities (Task, Category)
├── dto/             # Data Transfer Objects (TaskDto)
├── enums/           # TaskStatus, TaskPriority
├── exception/       # Custom exceptions & handlers
└── TodolistApplication.java

src/main/resources/
├── templates/       # Thymeleaf templates
│   ├── layout/      # Base layout (base.html)
│   ├── tasks/       # Task views (list, detail, form)
│   └── error/       # Error pages (404, 500)
├── db/migration/    # Flyway SQL migrations
└── application.yml  # Configuration

src/test/java/       # Tests (48 tests passing)
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tasks` | List all tasks (with filtering) |
| GET | `/tasks/{id}` | View task detail |
| GET | `/tasks/new` | Create task form |
| POST | `/tasks` | Create new task |
| GET | `/tasks/{id}/edit` | Edit task form |
| POST | `/tasks/{id}` | Update task |
| POST | `/tasks/{id}/delete` | Soft delete task |
| POST | `/tasks/{id}/complete` | Mark task complete |

### Query Parameters (GET /tasks)

| Param | Description | Example |
|-------|-------------|---------|
| `status` | Filter by status | `?status=TODO` |
| `priority` | Filter by priority | `?priority=HIGH` |
| `search` | Search title/description | `?search=meeting` |

## Development

### Run Tests

```bash
# All tests
./mvnw test

# With coverage report
./mvnw clean verify
open target/site/jacoco/index.html

# Specific test class
./mvnw test -Dtest=TaskServiceTest
```

### TDD Workflow

1. **RED** - Write failing test
2. **GREEN** - Minimum code to pass
3. **REFACTOR** - Clean up
4. **COMMIT**

### Database Migrations

```bash
# Migrations are in src/main/resources/db/migration/
# V001__init_schema.sql
# V002__create_tasks_table.sql
# V003__create_categories_table.sql
```

## Roadmap

| Phase | Status | Description |
|-------|--------|-------------|
| Phase 0 | ✅ Complete | Foundation - Project setup, DB, Security |
| Phase 1 | ✅ Complete | Basic Task Management - CRUD, filtering |
| Phase 2 | ⏳ Pending | User Management - Registration, login |
| Phase 3 | ⏳ Pending | Collaboration - Teams, assignment |
| Phase 4 | ⏳ Pending | Notifications - Email reminders |

## Documentation

| Document | Description |
|----------|-------------|
| [`CLAUDE.md`](CLAUDE.md) | AI coding instructions |
| [`docs/01-product-spec.md`](docs/01-product-spec.md) | Features & scope |
| [`docs/02-architecture.md`](docs/02-architecture.md) | System architecture |
| [`docs/03-implementation-plan.md`](docs/03-implementation-plan.md) | Implementation plan |
| [`docs/adr/`](docs/adr/) | Architecture Decision Records |

## Environment Variables

```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=todolist_db
DB_USERNAME=root
DB_PASSWORD=your_password

# Email (Phase 4)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## Commands Reference

```bash
./mvnw spring-boot:run                    # Run app (default profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev  # Run with dev profile
./mvnw test                               # Run all tests
./mvnw clean verify                       # Build + test + coverage
docker-compose up -d                      # Start MySQL + phpMyAdmin
docker-compose down                       # Stop containers
docker-compose logs -f                    # View logs
```

## Contributing

1. Follow TDD workflow
2. Maintain test coverage > 70%
3. Update documentation for new features
4. Follow existing code conventions

## License

MIT
