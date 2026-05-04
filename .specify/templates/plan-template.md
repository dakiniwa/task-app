# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [e.g., library/cli/web-service/mobile-app/compiler/desktop-app or NEEDS CLARIFICATION]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Source alignment**: Confirm the nearest `AGENTS.md`,
  `docs/task_app_design_detailed.md`, and relevant `docs/` feature files have
  been read. Record any conflicts and resolve stale docs before implementation.
- **Backend API first**: Plan backend domain, repository, service, controller,
  DTOs, API contract, and API verification before frontend integration tasks.
- **REST/user scope**: Task endpoints use plural resource paths, contain no
  verbs, and include `userId`, e.g. `/users/{userId}/tasks/{taskId}`.
- **Domain invariants**: Task status remains `TODO` / `DOING` / `DONE`; delete
  behavior is logical delete; task timestamps and `userId` are preserved.
- **Boundary separation**: Backend Entity/Enum and DTO types are separate;
  controller calls service only; service calls repository only; frontend API DTO
  types and UI state are separate.
- **Validation and observability**: DTO validation, service business validation,
  common error responses, 4xx/5xx logging, and API/manual verification are
  included for success, validation failure, not-found, and delete flows.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# Web application layout for this project
backend/
├── src/main/java/com/example/taskapp/
│   ├── task/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── domain/
│   │   ├── repository/
│   │   └── dto/
│   └── common/
│       ├── exception/
│       ├── config/
│       └── util/
└── src/test/

frontend/
├── src/
│   ├── app/
│   ├── features/
│   │   └── tasks/
│   │       ├── components/
│   │       ├── api/
│   │       ├── types/
│   │       └── hooks/
│   └── shared/
└── [tests if configured]

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
