# Product Specification

## Target Users

- Profesional yang mengatur tugas harian
- Tim kecil yang berkolaborasi
- Pengguna umum (tidak perlu background teknis)

## Problems Solved

- Lupa tugas dan deadline
- Sulit prioritaskan pekerjaan
- Tidak ada reminder efektif
- Sulit kolaborasi tim

## MVP Features

### Phase 1: Basic Task Management

| Feature | Status | Description |
|---------|--------|-------------|
| CRUD Tasks | Must | Create, view, edit, delete tugas |
| Categories | Must | Pengelompokan tugas dengan warna |
| Priority | Must | LOW, MEDIUM, HIGH, URGENT |
| Due Date | Must | Deadline dengan waktu |
| Filtering | Must | Filter by status, priority, category |
| Search | Must | Full-text search title & description |
| Pagination | Must | 20 items per page |

### Phase 2: User Management

| Feature | Status | Description |
|---------|--------|-------------|
| Registration | Must | Email + password |
| Email Verification | Must | Verifikasi sebelum akses penuh |
| Login/Logout | Must | Session-based auth |
| Password Reset | Must | Reset via email |
| User Profile | Must | Edit name, email, password |
| Dashboard | Must | Statistics & upcoming tasks |
| Account Lockout | Must | 5 failed attempts = 15 min lock |

### Phase 3: Collaboration

| Feature | Status | Description |
|---------|--------|-------------|
| Teams | Should | Create team, invite members |
| Roles | Should | OWNER, MEMBER, VIEWER |
| Task Assignment | Should | Assign task ke member |
| Comments | Should | Diskusi pada tugas |
| Activity Log | Should | Track semua perubahan |
| Task Sharing | Should | Share personal task ke user lain |

### Phase 4: Notifications

| Feature | Status | Description |
|---------|--------|-------------|
| Email Reminder | Should | 1h, 1d, 1w sebelum due date |
| In-App Notifications | Should | Bell icon + badge |
| Assignment Notification | Should | Email saat di-assign |
| Comment Notification | Should | Email saat ada comment baru |

## Out of Scope (v1.0)

**JANGAN implement fitur berikut:**

| Feature | Reason | Alternative |
|---------|--------|-------------|
| Recurring tasks | Complex scheduling | Manual create |
| Subtasks | Recursive structure | Use description checklist |
| File attachments | Storage management | Link to Drive/Dropbox |
| Calendar view | Complex UI | List with due date sort |
| Kanban board | Drag-drop complexity | Status column in table |
| Time tracking | Needs timer/reporting | Note in description |
| Custom fields | Dynamic schema | Use fixed fields |
| Public API | Security/versioning | Web UI only |
| Mobile app | Separate codebase | Responsive web |
| Real-time collab | WebSocket infra | Refresh to see updates |
| AI features | ML cost/complexity | Manual management |
| Multi-language | Translation overhead | EN/ID hardcoded |
| Themes | CSS management | Default theme only |
| Import/Export | File parsing | Manual entry |

## Constraints

### Performance
- Page load: < 2 seconds
- API response: < 500ms
- Search: < 1 second

### Scalability (MVP)
- Max 1000 users
- Max 100 tasks per user
- Max 10 team members
- Max 50 notifications/day/user

### Browser Support
- Chrome, Firefox, Safari, Edge (last 2 versions)
- No IE11

### Device Support
- Desktop: 1920x1080 (primary)
- Tablet: 768x1024
- Mobile: 375x667 (minimum)

## Success Metrics

**MVP berhasil jika:**
- Create task < 30 detik
- Registration flow < 2 menit
- Response time < 2 detik
- 80% user complete onboarding
- 70% tasks selesai on-time

## Reference

- Database Schema: [`adr/002-database-design.md`](adr/002-database-design.md)
- Implementation: [`03-implementation-plan.md`](03-implementation-plan.md)
