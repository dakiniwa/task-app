<!--
Sync Impact Report
Version change: template -> 1.0.0
Modified principles:
- Template principle 1 -> I. Source-of-Truth First
- Template principle 2 -> II. Backend API First
- Template principle 3 -> III. RESTful User-Scoped Contracts
- Template principle 4 -> IV. Strict Layer and Model Boundaries
- Template principle 5 -> V. Validated Data Lifecycle
Added sections:
- Project Validation Gates
- Technology and Workflow Constraints
Removed sections:
- Template placeholder comments and examples
Templates requiring updates:
- ✅ .specify/templates/plan-template.md
- ✅ .specify/templates/spec-template.md
- ✅ .specify/templates/tasks-template.md
- ✅ .specify/templates/commands/*.md (no command templates present)
- ✅ AGENTS.md, backend/AGENTS.md, frontend/AGENTS.md (reviewed; no changes required)
- ✅ docs/task_app_design_detailed.md (reviewed; no changes required)
- ✅ docs/team-tasks-lite/phase1-mvp/spec.md
- ✅ docs/team-tasks-lite/phase1-mvp/contracts.md
- ✅ docs/team-tasks-lite/phase1-mvp/validation.md
- ✅ docs/team-tasks-lite/phase1-mvp/jira-import.csv
Follow-up TODOs: None
-->
# Task App Constitution

## Core Principles

### I. Source-of-Truth First

All work MUST follow the nearest `AGENTS.md` for the files being changed. Feature
behavior, API contracts, and validation rules MUST be checked against
`docs/task_app_design_detailed.md` and the relevant files under `docs/` before
implementation. If project guidance conflicts, the nearest `AGENTS.md` and the
current detailed design take precedence; stale feature documents MUST be updated
before code is written.

Rationale: the project intentionally splits global, backend, and frontend agent
rules, so every change needs a single, explicit source chain.

### II. Backend API First

Each feature MUST be implemented as a small vertical increment, with the backend
REST API and its verification completed before frontend integration starts.
Frontend work MUST consume the backend API through `NEXT_PUBLIC_API_BASE_URL`
and MUST NOT duplicate backend business rules beyond lightweight pre-submit
validation.

Rationale: backend-first delivery keeps API behavior testable and prevents the
frontend from becoming the source of truth for domain behavior.

### III. RESTful User-Scoped Contracts

Task APIs MUST use plural resource URLs, MUST NOT include verbs, and MUST include
`userId` in the path for user-owned task resources:

- `GET /users/{userId}/tasks`
- `GET /users/{userId}/tasks/{taskId}`
- `POST /users/{userId}/tasks`
- `PUT /users/{userId}/tasks/{taskId}`
- `DELETE /users/{userId}/tasks/{taskId}`

Task status values MUST be limited to `TODO`, `DOING`, and `DONE`. New statuses,
ad hoc endpoint names such as `/createTask`, and user-agnostic task endpoints are
constitution violations unless this constitution and the source design are
amended first.

Rationale: stable REST contracts allow backend tests, frontend clients, and
future sprint work to evolve without endpoint drift.

### IV. Strict Layer and Model Boundaries

Backend code MUST preserve the modular monolith package structure under
`com.example.taskapp`. Controllers MUST call services only, services MUST call
repositories only, domain entities and enums MUST stay separate from DTOs, and
shared exceptions/configuration/utilities MUST live under `common`.

Frontend code MUST keep API access out of screen components, keep API DTO types
separate from UI state, and place task-specific client code under the task
feature area when a feature module exists.

Rationale: layer and model separation prevents persistence concerns, API
contracts, and UI state from leaking across ownership boundaries.

### V. Validated Data Lifecycle

Task data MUST preserve the detailed design model: `id: Long`, required `title`,
optional `description`, `status`, `userId`, `deleted`, `createdAt`, and
`updatedAt`. Deletes MUST be logical deletes through the `deleted` flag unless a
future amendment explicitly allows physical deletion for a defined case.

Input validation MUST be defined on request DTOs, business rules MUST be checked
in services, and API errors MUST use a consistent response shape. Backend changes
MUST include API-level verification for success paths, validation failures,
not-found cases, and logical delete behavior. 4xx and 5xx errors MUST be logged.

Rationale: task data is shared by persistence, API clients, and UI workflows; a
validated lifecycle keeps behavior observable and repeatable.

## Project Validation Gates

Every feature specification MUST identify the source documents it depends on and
MUST state any API path, task status, deletion, validation, or model-boundary
rules it touches. A feature is not ready for planning if these constraints are
missing or conflict with this constitution.

Every implementation plan MUST pass the Constitution Check before research and
again after design. The check MUST verify backend-first sequencing, REST
resource naming, `userId` path scope, DTO/entity separation, frontend DTO/UI
state separation, logical deletion, timestamp handling, validation/error
handling, and required verification.

Every generated task list MUST preserve incremental delivery order: setup,
backend domain/API contract, backend verification, frontend integration, and
end-to-end or manual acceptance verification. Any skipped gate MUST be recorded
as a justified complexity item with an owner and follow-up.

## Technology and Workflow Constraints

The backend MUST use Java, Spring Boot, Spring Data JPA, and PostgreSQL, with
the package responsibilities defined in `backend/AGENTS.md`. The frontend MUST
use Next.js and TypeScript, with task feature code organized according to
`frontend/AGENTS.md`.

Changes MUST stay small and feature-scoped. Documentation under `docs/` MUST be
updated in the same change when behavior, contracts, validation, or workflow
expectations change. Jira workflow status and application `Task.status` are
separate concepts and MUST NOT be mixed.

Future extensions such as JWT authentication, tags, team sharing, notifications,
or microservices MUST start from a spec update and MUST pass all validation
gates before implementation.

## Governance

This constitution supersedes informal practices. Runtime guidance in `AGENTS.md`
files remains binding and is interpreted through this constitution. If an
implementation, spec, template, or issue conflicts with the constitution, the
conflict MUST be resolved before implementation continues.

Amendments MUST include the reason for change, affected principles or sections,
updates to dependent templates and runtime docs, and a Sync Impact Report at the
top of this file. Versioning follows semantic versioning:

- MAJOR for principle removals, redefinitions, or backward-incompatible
  governance changes
- MINOR for new principles, new required gates, or materially expanded guidance
- PATCH for clarifications, wording fixes, and non-semantic refinements

Compliance MUST be reviewed during `/speckit-plan`, `/speckit-tasks`, code
review, and final verification. Reviewers MUST reject changes that add endpoint
verbs, extend task status values, mix DTOs with entities, bypass logical delete,
or skip required validation without an approved constitution amendment.

**Version**: 1.0.0 | **Ratified**: 2026-05-04 | **Last Amended**: 2026-05-04
