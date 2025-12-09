# ADR-004: Feature Scope

## Status

Accepted

## Context

TodoList perlu batasan scope yang jelas untuk MVP agar development tetap fokus.

## Decision

### MVP Scope (4 Phases)

| Phase | Focus | Timeline |
|-------|-------|----------|
| 0 | Foundation (project setup) | Week 1-2 |
| 1 | Task Management (CRUD, filter, search) | Week 3-4 |
| 2 | User Management (auth, dashboard) | Week 5-6 |
| 3 | Collaboration (teams, assignment) | Week 7-8 |
| 4 | Notifications (reminders, email) | Week 9-10 |

### Out of Scope (JANGAN implement)

| Feature | Reason |
|---------|--------|
| Recurring tasks | Complex scheduling |
| Subtasks | Recursive structure |
| File attachments | Storage management |
| Calendar view | Complex UI |
| Kanban board | Drag-drop complexity |
| Time tracking | Needs timer/reporting |
| Custom fields | Dynamic schema |
| Public API | Security/versioning overhead |
| Mobile app (native) | Separate codebase |
| Real-time collab | WebSocket infrastructure |
| AI features | ML cost/complexity |
| Multi-language (i18n) | Translation overhead |
| Custom themes | CSS management |
| Import/Export | File parsing |

### Constraints

**Performance:**
- Page load: < 2 seconds
- API response: < 500ms

**Scale (MVP):**
- Max 1000 users
- Max 100 tasks/user
- Max 10 team members

**Browser:**
- Chrome, Firefox, Safari, Edge (last 2 versions)
- No IE11

## Reference

- Product Spec: [`../01-product-spec.md`](../01-product-spec.md)
- Implementation: [`../03-implementation-plan.md`](../03-implementation-plan.md)
