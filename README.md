# TodoList App

Web-based task management application built with Spring Boot 3.4 + Thymeleaf + MySQL.

**Status:** Phase 2 - User Management (In Progress)

## Features (Current)

### Core Task Management
- Task CRUD (Create, Read, Update, Delete)
- Task filtering by status and priority
- Task search by title/description
- Soft delete (archive) tasks
- Mark tasks as complete

### User Management
- **Authentication**: Secure Registration & Login
- **Profile Management**: Edit Full Name
- **Security**: Change Password (with "Reveal" toggle)
- **Dashboard**: Personal analytics and quick actions

### UI/UX
- **Modern Design**: "HealDocs / Premium Blue" theme
- **Responsive Layout**: Glassmorphism Sidebar & Sticky Header
- **Interactivity**: Alpine.js transformations & Toast Notifications
- **Datepicker**: Integrated Flatpickr

## Screenshots

| Dashboard | Task List | Task Detail |
|-----------|-----------|-------------|
| Analytics & Quick Access | Grid/List view with filters | Detailed view & actions |

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

**Default credentials:** `user` / (check console for generated password) or Register a new account.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.4.1, Java 21 |
| Frontend | Thymeleaf, Tailwind CSS, Alpine.js, Flatpickr |
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
├── controller/      # MVC controllers (TaskController, UserController)
├── service/         # Business logic (TaskService, UserService)
├── repository/      # Spring Data JPA repositories
├── entity/          # JPA entities (Task, User)
├── dto/             # Data Transfer Objects (TaskDto, UserDto)
├── enums/           # TaskStatus, TaskPriority
├── exception/       # Custom exceptions & handlers
└── TodolistApplication.java

src/main/resources/
├── templates/       # Thymeleaf templates
│   ├── layout/      # Base layout (base.html)
│   ├── tasks/       # Task views (list, detail, form)
│   ├── user/        # User views (profile, dashboard)
│   └── auth/        # Auth views (login, register)
└── application.yml  # Configuration
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tasks` | List all tasks (with filtering) |
| GET | `/tasks/new` | Create task form |
| POST | `/tasks` | Create new task |
| GET | `/dashboard` | User Dashboard |
| GET | `/profile` | User Profile Page |
| GET | `/profile/edit` | Edit Profile Form |
| POST | `/profile/change-password` | Change Password |

## Roadmap

| Phase | Status | Description |
|-------|--------|-------------|
| Phase 0 | ✅ Complete | Foundation - Project setup, DB, Security |
| Phase 1 | ✅ Complete | Basic Task Management - CRUD, filtering |
| Phase 2 | 🏗 In Progress | User Management - Auth, Profile, Dashboard |
| Phase 3 | ⏳ Pending | Collaboration - Teams, assignment |
| Phase 4 | ⏳ Pending | Notifications - Email reminders |

## Documentation

| Document | Description |
|----------|-------------|
| [`CLAUDE.md`](CLAUDE.md) | AI coding instructions |
| [`docs/01-product-spec.md`](docs/01-product-spec.md) | Features & scope |
| [`docs/02-architecture.md`](docs/02-architecture.md) | System architecture |
| [`docs/03-implementation-plan.md`](docs/03-implementation-plan.md) | Implementation plan |
| [`docs/04-ui-ux-design-system.md`](docs/04-ui-ux-design-system.md) | **NEW:** UI/UX Design System Guide |
| [`docs/adr/`](docs/adr/) | Architecture Decision Records |

## License

MIT
