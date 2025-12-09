# ADR-001: Core Architecture

## Status

Accepted

## Context

TodoList adalah aplikasi web-based task management untuk individual users dan tim kecil.

## Decision

### Tech Stack

| Component | Choice | Reason |
|-----------|--------|--------|
| Framework | Spring Boot 4.0 | Mature ecosystem, built-in security |
| Language | Java 21 | Virtual threads, LTS until 2029 |
| Frontend | Thymeleaf + HTMX | Server-side rendering, simple interactivity |
| Database | MySQL 8.0 | ACID compliance, familiar, good read performance |
| Security | Spring Security 6 | Session-based auth (bukan JWT) |
| Email | Spring Mail | @Async sending |
| Migration | Flyway | Versioned SQL migrations |

### Architecture: Layered Monolith

```
Presentation (Controllers + Views)
        ↓
Security (Spring Security Filter Chain)
        ↓
Service (Business Logic)
        ↓
Repository (Spring Data JPA)
        ↓
Database (MySQL)
```

### Why Session-Based (bukan JWT)?

- TodoList adalah traditional web app, bukan API-first
- Session lebih simple untuk server-rendered apps
- Built-in support di Spring Security
- Lebih secure (HttpOnly cookies)

### Why Monolith (bukan Microservices)?

- Lebih simple untuk MVP
- Easy to deploy & debug
- Bisa di-refactor nanti jika needed

## Consequences

**Positive:**
- Simple deployment (single JAR)
- Type safety (Java strong typing)
- Mature ecosystem & community
- Transaction support (ACID)
- Security by default

**Negative:**
- JVM memory overhead
- Slower startup vs Node.js
- Less interactive than SPA (mitigasi: HTMX)

## Reference

- Architecture details: [`../02-architecture.md`](../02-architecture.md)
