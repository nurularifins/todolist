# 6. UI/E2E Testing Strategy

Date: 2025-12-19

## Status

Accepted

## Context

The application has moved beyond simple server-side rendering to include dynamic frontend behavior using Alpine.js (e.g., Sidebar toggles, Dropdowns, potentially HTMX later).

Existing testing tools:
-   **JUnit 5 / Mockito**: Unit tests for Services/Domain.
-   **MockMvc (@WebMvcTest)**: Integration tests for Controllers.

Limitations:
-   MockMvc does not execute JavaScript.
-   It cannot verify the actual visual layout (CSS) or client-side interactivity (Alpine.js state).
-   Manual testing is repetitive and error-prone for UI flows.

## Decision

We will adopt **Playwright** for End-to-End (E2E) and UI testing.

### Key Reasons:
1.  **Reliability**: Auto-waiting mechanism reduces flaky tests compared to Selenium.
2.  **Speed**: Parallel execution by default.
3.  **Tooling**: Built-in screenshot/video capture and trace viewer are excellent for debugging.
4.  **Language**: We will write tests in **TypeScript/JavaScript** to leverage the rich ecosystem of Playwright, running alongside the Java backend.

## Consequences

### Positive
-   Confidence in UI functionality across browsers (Chromium, Firefox, WebKit).
-   Automated visual regression testing via screenshots.
-   Ability to test mobile responsiveness (sidebar toggles).

### Negative
-   **New Dependency**: Requires Node.js and NPM in the build environment.
-   **Complexity**: Two conflicting ecosystems (Java/Maven vs Node/NPM). We will treat the Playwright suite as a separate "module" or script `npm run e2e` that assumes the Spring Boot app is already running.

## Implementation Strategy
1.  Initialize Playwright in root (or `frontend-tests` folder).
2.  Define `baseURL` in playwright config to point to `http://localhost:8080`.
3.  Create helper scripts to seed database (using `DataSeeder` or API endpoints) before test runs to ensure deterministic states.
