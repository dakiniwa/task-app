<!--
Sync Impact Report
- Version change: N/A -> 1.0.0
- Modified principles:
  - Template Principle 1 -> I. Simplicity First
  - Template Principle 2 -> II. Consistency by Design
  - Template Principle 3 -> III. Backend-First Delivery
  - Template Principle 4 -> IV. Specification as Single Source of Truth
  - Template Principle 5 -> V. Clear Boundaries and Extensibility
- Added sections:
  - Product Context
  - Development Workflow
- Removed sections:
  - None
- Templates requiring updates:
  - ✅ .specify/templates/plan-template.md
  - ✅ .specify/templates/spec-template.md
  - ✅ .specify/templates/tasks-template.md
  - ✅ .specify/extensions/git/commands/speckit.git.initialize.md
- Follow-up TODOs:
  - None
-->
# Task App Constitution

## Core Principles

### I. Simplicity First
All design and implementation decisions MUST prioritize a lightweight and simple
user experience. Teams MUST avoid unnecessary abstractions, features, and
dependencies unless they are required by current validated needs.
Rationale: The primary product goal is personal productivity with minimal
cognitive load.

### II. Consistency by Design
System behavior, naming, and architecture MUST remain consistent across backend,
frontend, and documentation. Any new implementation MUST align with established
patterns before introducing a new one.
Rationale: Consistency reduces defects, accelerates delivery, and keeps the
codebase maintainable as the product grows.

### III. Backend-First Delivery
Feature implementation MUST proceed in the order of backend API completion
before frontend integration. API contracts MUST be defined and validated before
UI logic consumes them.
Rationale: A stable service contract enables parallel frontend work later and
prevents coupling UI behavior to unstable backend assumptions.

### IV. Specification as Single Source of Truth
All implementation decisions MUST trace to artifacts under `.specify/`.
If a requirement is not defined in `.specify/`, it is treated as undefined and
MUST be clarified before implementation. Assumptions MUST be explicit in spec
artifacts.
Rationale: Specification-driven development requires clear, auditable decisions
to avoid implicit scope expansion.

### V. Clear Boundaries and Extensibility
Responsibilities across layers MUST remain separated: backend domain and DTOs
MUST NOT be mixed, and frontend API DTOs and UI state models MUST remain
distinct. Architecture MUST support future business-scale expansion without
adding current over-engineering.
Rationale: Strict boundaries preserve adaptability and reduce regression risk
when extending from hobby use to business use cases.

## Product Context

- Target product: a personal task management application for hobby use.
- Architecture: modular monolith.
- Technology stack:
  - Backend: Java, Spring Boot, Spring Data JPA
  - Frontend: Next.js, TypeScript
  - Database: PostgreSQL
- API policy:
  - REST-compliant resource-oriented endpoints
  - Resource paths use plural nouns
  - URL paths MUST NOT include verbs
  - Data is managed by user scope
- Domain constraints:
  - Task status values are restricted to `TODO`, `DOING`, and `DONE`
  - Deletion is logical delete by default

## Development Workflow

- Development process MUST be agile and incremental.
- Delivery slices MUST be small, testable, and expandable.
- Teams MUST perform recognition alignment before implementation when
  requirements are ambiguous.
- Unknown requirements MUST be clarified; speculation is prohibited.
- AI-assisted changes MUST ask at most one clarification question at a time.
- Changes MUST preserve priority order: simplicity, consistency, extensibility.

## Governance

This constitution overrides conflicting process guidance. Every plan, spec, and
task artifact MUST include a constitution alignment check before implementation.

Amendment process:
- Any amendment MUST include rationale, impacted principles, and migration notes
  for dependent templates or guidance.
- Amendments MUST be approved before new behavior based on them is implemented.
- Constitutional changes MUST be propagated to affected templates and command
  guidance in the same update cycle or explicitly tracked as follow-up tasks.

Versioning policy:
- Semantic versioning is mandatory for this constitution.
- MAJOR: incompatible governance changes or principle removals/redefinitions.
- MINOR: new principles or materially expanded governance requirements.
- PATCH: wording clarification or non-semantic refinements.

Compliance review:
- Each feature workflow (`/speckit-specify`, `/speckit-plan`, `/speckit-tasks`)
  MUST confirm constitution compliance.
- Reviews MUST reject work that introduces undefined requirements, responsibility
  mixing, or violations of backend-first/API policy.

**Version**: 1.0.0 | **Ratified**: 2026-05-05 | **Last Amended**: 2026-05-05
