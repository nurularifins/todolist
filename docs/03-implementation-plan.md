# Implementation Plan (TDD-First)

## TDD Workflow (WAJIB untuk setiap task)

```
1. RED    → Tulis test yang FAIL berdasarkan acceptance criteria
2. GREEN  → Implementasi MINIMUM untuk pass test
3. REFACTOR → Clean up code, tests tetap pass
4. COMMIT → Commit dengan format: test:, feat:, refactor:
```

---

## Phase 0: Foundation

**Goal:** Project setup, infrastructure siap

### Deliverables

#### 0.1 Project Setup
- [ ] Spring Boot 4.0 project (Spring Initializr)
- [ ] Maven dependencies configured
- [ ] Multi-profile: dev, staging, prod
- [ ] application.yml configured

#### 0.2 Database Setup
- [ ] Docker Compose (MySQL + phpMyAdmin)
- [ ] Flyway configured
- [ ] V001__init_schema.sql created
- [ ] Connection tested

#### 0.3 Frontend Setup
- [ ] Thymeleaf integrated
- [ ] Tailwind CSS (via CDN)
- [ ] HTMX + Alpine.js added
- [ ] Base layout template created

#### 0.4 Test Infrastructure
- [ ] JUnit 5 + Mockito configured
- [ ] AssertJ added
- [ ] Testcontainers configured
- [ ] JaCoCo coverage setup

#### 0.5 Code Analysis
- [ ] SonarCloud project setup
- [ ] GitHub Actions workflow for SonarCloud
- [ ] Quality gate configured

#### 0.6 Error Handling
- [ ] GlobalExceptionHandler (@ControllerAdvice)
- [ ] Error pages: 404, 403, 500

### Acceptance Criteria
```
./mvnw spring-boot:run → starts without error
./mvnw test → runs without error
docker-compose up → MySQL accessible
http://localhost:8080 → shows homepage
```

---

## Phase 1: Basic Task Management

**Goal:** User dapat CRUD tasks dengan filtering

### 1.1 Task Entity & Repository (TDD)

**Tests to Write FIRST:**
```java
// TaskRepositoryTest.java
@Test void shouldSaveTask()
@Test void shouldFindTaskById()
@Test void shouldFindTasksByStatus()
@Test void shouldFindTasksByPriority()
@Test void shouldSearchByTitleOrDescription()
@Test void shouldPaginateResults()
```

**Implementation:**
- [ ] V002__create_tasks_table.sql
- [ ] Task entity (UUID, title, description, status, priority, dueDate, timestamps)
- [ ] TaskStatus enum (TODO, IN_PROGRESS, DONE)
- [ ] TaskPriority enum (LOW, MEDIUM, HIGH, URGENT)
- [ ] TaskRepository with custom queries

### 1.2 Category Entity & Repository (TDD)

**Tests to Write FIRST:**
```java
// CategoryRepositoryTest.java
@Test void shouldSaveCategory()
@Test void shouldFindCategoriesByUser()
@Test void shouldEnforceUniqueCategoryNamePerUser()
```

**Implementation:**
- [ ] V003__create_categories_table.sql
- [ ] Category entity (UUID, name, color, userId)
- [ ] CategoryRepository

### 1.3 Task Service (TDD)

**Tests to Write FIRST:**
```java
// TaskServiceTest.java
@Test void shouldCreateTaskSuccessfully()
@Test void shouldThrowExceptionWhenTitleNull()
@Test void shouldThrowExceptionWhenTitleBlank()
@Test void shouldThrowExceptionWhenDueDateInPast()
@Test void shouldUpdateTaskSuccessfully()
@Test void shouldThrowExceptionWhenTaskNotFound()
@Test void shouldDeleteTask()  // soft delete
@Test void shouldMarkTaskAsComplete()
```

**Implementation:**
- [ ] TaskService with CRUD operations
- [ ] TaskDto for request/response
- [ ] Validation logic
- [ ] ActivityLogService integration

### 1.4 Task Controller (TDD)

**Tests to Write FIRST:**
```java
// TaskControllerTest.java
@Test @WithMockUser void shouldCreateTask()
@Test @WithMockUser void shouldReturnValidationError()
@Test void shouldRedirectToLoginWhenNotAuthenticated()
@Test @WithMockUser void shouldProtectAgainstCSRF()
@Test @WithMockUser void shouldGetTaskList()
@Test @WithMockUser void shouldFilterByStatus()
```

**Implementation:**
- [ ] TaskController (REST/MVC)
- [ ] Form validation (@Valid)
- [ ] Flash messages

### 1.5 Task Views
- [ ] Task list page (paginated)
- [ ] Task detail page
- [ ] Create/Edit forms
- [ ] Filter sidebar
- [ ] Search bar
- [ ] HTMX inline editing

### Acceptance Criteria - Phase 1
```
Create task < 10 seconds
Task list loads < 1 second
Search returns < 2 seconds
Filter updates without page reload
All tests pass
Coverage > 70%
```

---

## Phase 2: User Management

**Goal:** Secure authentication & user dashboard

### 2.1 User Entity & Repository (TDD)

**Tests to Write FIRST:**
```java
// UserRepositoryTest.java
@Test void shouldSaveUser()
@Test void shouldFindByEmail()
@Test void shouldEnforceUniqueEmail()
@Test void shouldFindByVerificationToken()
@Test void shouldFindByResetToken()
```

**Implementation:**
- [ ] V004__create_users_table.sql
- [ ] User entity (UUID, email, passwordHash, fullName, isVerified, tokens)
- [ ] UserRepository

### 2.2 User Service (TDD)

**Tests to Write FIRST:**
```java
// UserServiceTest.java
@Test void shouldRegisterUser()
@Test void shouldThrowExceptionWhenEmailExists()
@Test void shouldHashPassword()
@Test void shouldVerifyEmail()
@Test void shouldThrowExceptionWhenTokenInvalid()
@Test void shouldResetPassword()
@Test void shouldLockAccountAfter5FailedAttempts()
```

**Implementation:**
- [ ] UserService (register, verify, reset)
- [ ] UserDto
- [ ] Password validation (8 chars, upper, lower, number, special)
- [ ] Token generation

### 2.3 Security Configuration (TDD)

**Tests to Write FIRST:**
```java
// SecurityConfigTest.java
@Test void shouldAllowPublicEndpoints()
@Test void shouldRequireAuthForTasks()
@Test void shouldRedirectToLoginWhenUnauthenticated()
@Test void shouldEnforceCSRF()
@Test void shouldLogout()
```

**Implementation:**
- [ ] SecurityConfig
- [ ] CustomUserDetailsService
- [ ] BCryptPasswordEncoder (strength 10)
- [ ] Session configuration
- [ ] Remember-me

### 2.4 Auth Controllers (TDD)

**Tests to Write FIRST:**
```java
// AuthControllerTest.java
@Test void shouldShowRegistrationForm()
@Test void shouldRegisterUser()
@Test void shouldVerifyEmail()
@Test void shouldShowLoginForm()
@Test void shouldResetPassword()
```

**Implementation:**
- [ ] AuthController (register, login, verify)
- [ ] PasswordResetController

### 2.5 Email Service (TDD)

**Tests to Write FIRST:**
```java
// EmailServiceTest.java
@Test void shouldSendVerificationEmail()
@Test void shouldSendPasswordResetEmail()
@Test void shouldRetryOnFailure()
```

**Implementation:**
- [ ] EmailService (@Async)
- [ ] Email templates (Thymeleaf)

### 2.6 Views
- [ ] Registration form
- [ ] Login form
- [ ] Email verification page
- [ ] Password reset forms
- [ ] User profile page
- [ ] Dashboard with statistics

### Acceptance Criteria - Phase 2
```
Registration flow < 2 minutes
Email verification works
Login redirects to dashboard
Password reset email < 1 minute
Account lockout after 5 attempts
All tests pass
Coverage > 70%
```

---

## Phase 3: Collaboration

**Goal:** Team workspace & task sharing

### 3.1 Team Entity & Repository (TDD)

**Tests to Write FIRST:**
```java
// TeamRepositoryTest.java
@Test void shouldSaveTeam()
@Test void shouldFindTeamsByUser()
@Test void shouldFindTeamMembers()
```

**Implementation:**
- [ ] V005__create_teams_table.sql
- [ ] V006__create_team_members_table.sql
- [ ] Team, TeamMember entities
- [ ] TeamRole enum (OWNER, MEMBER, VIEWER)

### 3.2 Team Service (TDD)

**Tests to Write FIRST:**
```java
// TeamServiceTest.java
@Test void shouldCreateTeam()
@Test void shouldInviteMember()
@Test void shouldAcceptInvitation()
@Test void shouldRemoveMember()
@Test void shouldDeleteTeam()
@Test void shouldEnforceOwnerOnlyForDeletion()
```

**Implementation:**
- [ ] TeamService
- [ ] TeamMemberService
- [ ] TeamDto

### 3.3 Task Assignment (TDD)

**Tests to Write FIRST:**
```java
// TaskAssignmentServiceTest.java
@Test void shouldAssignTask()
@Test void shouldUnassignTask()
@Test void shouldSendNotificationOnAssignment()
@Test void shouldEnforceMembershipForAssignment()
```

**Implementation:**
- [ ] V007__create_task_assignments_table.sql
- [ ] TaskAssignment entity
- [ ] TaskAssignmentService

### 3.4 Comments (TDD)

**Tests to Write FIRST:**
```java
// TaskCommentServiceTest.java
@Test void shouldAddComment()
@Test void shouldEditOwnComment()
@Test void shouldDeleteOwnComment()
@Test void shouldNotEditOthersComment()
```

**Implementation:**
- [ ] V008__create_task_comments_table.sql
- [ ] TaskComment entity
- [ ] TaskCommentService

### 3.5 Activity Log (TDD)

**Tests to Write FIRST:**
```java
// ActivityLogServiceTest.java
@Test void shouldLogTaskCreated()
@Test void shouldLogTaskUpdated()
@Test void shouldLogTaskAssigned()
@Test void shouldLogCommentAdded()
```

**Implementation:**
- [ ] V009__create_activity_logs_table.sql
- [ ] ActivityLog entity
- [ ] ActivityLogService

### 3.6 Controllers & Views
- [ ] TeamController
- [ ] Team list/detail pages
- [ ] Invite member modal
- [ ] Task assignment UI
- [ ] Comment section
- [ ] Activity timeline

### Acceptance Criteria - Phase 3
```
Team creation & invitation works
Assigned tasks show in assignee's list
Comments visible on task detail
Activity log shows all changes
Non-members get 403
All tests pass
Coverage > 70%
```

---

## Phase 4: Notifications

**Goal:** Timely reminders & notifications

### 4.1 Reminder Entity & Repository (TDD)

**Tests to Write FIRST:**
```java
// TaskReminderRepositoryTest.java
@Test void shouldSaveReminder()
@Test void shouldFindPendingReminders()
@Test void shouldMarkAsSent()
```

**Implementation:**
- [ ] V010__create_task_reminders_table.sql
- [ ] TaskReminder entity
- [ ] ReminderType enum (EMAIL, IN_APP)

### 4.2 Notification Entity & Repository (TDD)

**Tests to Write FIRST:**
```java
// NotificationRepositoryTest.java
@Test void shouldSaveNotification()
@Test void shouldFindUnreadByUser()
@Test void shouldMarkAsRead()
@Test void shouldCountUnread()
```

**Implementation:**
- [ ] V011__create_notifications_table.sql
- [ ] Notification entity
- [ ] NotificationType enum

### 4.3 Reminder Service (TDD)

**Tests to Write FIRST:**
```java
// ReminderServiceTest.java
@Test void shouldCreateReminder()
@Test void shouldProcessPendingReminders()
@Test void shouldSendEmailForPendingReminder()
@Test void shouldMarkReminderAsSent()
```

**Implementation:**
- [ ] ReminderService
- [ ] ReminderScheduler (@Scheduled)

### 4.4 Notification Service (TDD)

**Tests to Write FIRST:**
```java
// NotificationServiceTest.java
@Test void shouldCreateNotification()
@Test void shouldMarkAsRead()
@Test void shouldGetUnreadCount()
```

**Implementation:**
- [ ] NotificationService
- [ ] NotificationController

### 4.5 Scheduled Jobs
- [ ] Check reminders (every hour)
- [ ] Cleanup expired sessions (daily)
- [ ] Cleanup old notifications (weekly)
- [ ] Cleanup archived tasks > 30 days (monthly)

### 4.6 Views
- [ ] Notification dropdown (bell icon)
- [ ] Unread badge
- [ ] Reminder settings page
- [ ] Email templates

### Acceptance Criteria - Phase 4
```
Reminder emails sent on schedule
In-app notifications appear
Badge shows unread count
Mark as read works
Scheduled jobs run without error
All tests pass
Coverage > 70%
```

---

## Test Coverage Requirements

| Layer | Minimum | Target |
|-------|---------|--------|
| Service | 90% | 100% |
| Repository | 70% | 90% |
| Controller | 80% | 95% |
| Overall | **70%** | 85% |

## Commit Convention

```bash
# After writing failing test
git commit -m "test: add failing test for TaskService.createTask"

# After making test pass
git commit -m "feat: implement TaskService.createTask"

# After refactoring
git commit -m "refactor: extract validation to TaskValidator"
```

## Reference

- Product Spec: [`01-product-spec.md`](01-product-spec.md)
- Database Schema: [`adr/002-database-design.md`](adr/002-database-design.md)
- TDD Details: [`adr/005-tdd-approach.md`](adr/005-tdd-approach.md)
