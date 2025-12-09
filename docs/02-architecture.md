# Architecture

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 4.0, Java 21 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Security | Spring Security 6 (session-based) |
| Migration | Flyway |
| Code Analysis | JaCoCo + SonarCloud |
| Email | Spring Mail + @Async |
| Scheduler | Spring Scheduler |
| Build | Maven |
| Frontend | Thymeleaf + Tailwind CSS + HTMX + Alpine.js |
| Container | Docker + Docker Compose |

## Layered Architecture

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (Controllers + Thymeleaf Views)    │
│  • @Controller, @Valid              │
│  • Form handling, redirects         │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Security Layer                 │
│  (Spring Security Filter Chain)     │
│  • CSRF, Session, Auth filters      │
│  • @PreAuthorize                    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Service Layer                  │
│  (Business Logic + Validation)      │
│  • @Service, @Transactional         │
│  • DTOs, exceptions                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│    Repository Layer                 │
│  (Spring Data JPA)                  │
│  • JpaRepository interfaces         │
│  • Custom @Query methods            │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│       Database (MySQL)              │
│  • Flyway migrations                │
│  • Indexes, constraints             │
└─────────────────────────────────────┘
```

## Project Structure

```
src/main/java/com/nurularifins/todolist/
├── config/          # SecurityConfig, EmailConfig, etc.
├── controller/      # TaskController, AuthController, etc.
├── service/         # TaskService, UserService, etc.
├── repository/      # TaskRepository, UserRepository, etc.
├── entity/          # Task, User, Team, etc.
├── dto/             # TaskDto, UserDto, etc.
├── enums/           # TaskStatus, TaskPriority, TeamRole
├── security/        # CustomUserDetailsService, TaskSecurityService
├── exception/       # TaskNotFoundException, GlobalExceptionHandler
├── scheduler/       # ReminderScheduler, CleanupScheduler
└── TodolistApplication.java

src/main/resources/
├── templates/       # Thymeleaf HTML
├── db/migration/    # Flyway SQL files
├── static/          # CSS, JS, images
└── application.yml

src/test/java/       # Mirror structure untuk tests
```

## Key Design Decisions

### Session-based Auth (bukan JWT)
- TodoList adalah traditional web app, bukan API-first
- Session lebih simple untuk server-side rendering
- Built-in support di Spring Security
- Lebih secure by default (HttpOnly cookies)

### MySQL (bukan PostgreSQL/NoSQL)
- Sufficient untuk use case ini
- Familiar bagi banyak developer
- Good performance untuk read-heavy operations
- ACID compliance untuk data consistency

### Monolith (bukan Microservices)
- Lebih simple untuk MVP
- Easy to deploy & debug
- Bisa di-refactor nanti jika needed

### Server-Side Rendering (bukan SPA)
- Thymeleaf + HTMX untuk interactivity
- SEO friendly
- No CORS, token management complexity

## Security Architecture

```
Request → CSRF Filter → Session Filter → Auth Filter → Controller
                                                           ↓
                                              @PreAuthorize check
                                                           ↓
                                              Service (ownership check)
```

**Key Security Features:**
- BCrypt password hashing (strength 10)
- HttpOnly + Secure + SameSite cookies
- CSRF token on all forms
- Session timeout: 30 minutes
- Remember-me: 7 days
- Account lockout: 5 failed attempts

## Database Strategy

- **Primary Key:** UUID (security, distributed-friendly)
- **Timestamps:** DATETIME (no timezone issues)
- **Soft Delete:** `is_archived` flag
- **Audit:** created_at, updated_at, created_by, updated_by

See [`adr/002-database-design.md`](adr/002-database-design.md) for full schema.

## Performance Considerations

- HikariCP connection pool (max 10)
- Lazy loading untuk JPA relationships
- Pagination (20 items per page)
- Indexes pada frequently queried columns
- @Async untuk email sending

## Deployment

### Development
```bash
docker-compose up -d    # MySQL + phpMyAdmin
./mvnw spring-boot:run  # App di localhost:8080
```

### Production
- Single JAR deployment
- NGINX reverse proxy
- SSL via Let's Encrypt
- AWS RDS / Managed MySQL

## Reference

- Core Architecture ADR: [`adr/001-core-architecture.md`](adr/001-core-architecture.md)
- Database Design ADR: [`adr/002-database-design.md`](adr/002-database-design.md)
- Authentication ADR: [`adr/003-authentication.md`](adr/003-authentication.md)
