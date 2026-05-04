---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Include verification tasks required by the feature specification and
constitution. Backend API changes need tests or explicit manual verification for
success, validation failure, not-found, and logical-delete paths.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `backend/src/main/java/com/example/taskapp/`,
  `backend/src/test/`
- **Frontend**: `frontend/src/app/`, `frontend/src/features/tasks/`,
  `frontend/src/shared/`
- **Docs**: `docs/`, feature-specific files under `docs/team-tasks-lite/`
- Paths in generated tasks MUST use the concrete paths from plan.md

<!-- 
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.
  
  The /speckit-tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints from contracts/
  
  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment
  
  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan
- [ ] T002 Initialize [language] project with [framework] dependencies
- [ ] T003 [P] Configure linting and formatting tools

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Setup database schema and migrations framework
- [ ] T005 [P] Create backend package structure under `backend/src/main/java/com/example/taskapp/`
- [ ] T006 [P] Setup REST routing, validation, and common exception handling
- [ ] T007 Create Task domain model, DTO conventions, repository conventions, and logical delete support
- [ ] T008 Configure logging for 4xx/5xx API errors
- [ ] T009 Setup frontend API base URL handling with `NEXT_PUBLIC_API_BASE_URL`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 🎯 MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1

> **NOTE: Add backend API tests or manual verification tasks before implementation when this story changes API behavior.**

- [ ] T010 [P] [US1] Backend API verification for `[endpoint]` in `backend/src/test/...`
- [ ] T011 [P] [US1] Frontend/manual verification for `[user journey]` if UI changes

### Implementation for User Story 1

- [ ] T012 [P] [US1] Create/update domain Entity or Enum in `backend/src/main/java/com/example/taskapp/task/domain/`
- [ ] T013 [P] [US1] Create/update request and response DTOs in `backend/src/main/java/com/example/taskapp/task/dto/`
- [ ] T014 [US1] Implement repository query in `backend/src/main/java/com/example/taskapp/task/repository/`
- [ ] T015 [US1] Implement service logic and transaction boundary in `backend/src/main/java/com/example/taskapp/task/service/`
- [ ] T016 [US1] Implement REST controller endpoint in `backend/src/main/java/com/example/taskapp/task/controller/`
- [ ] T017 [US1] Add frontend API client/types in `frontend/src/features/tasks/api/` and `frontend/src/features/tasks/types/` if UI changes
- [ ] T018 [US1] Add/update UI components and hooks in `frontend/src/features/tasks/` if UI changes

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2

- [ ] T019 [P] [US2] Backend API verification for `[endpoint]` in `backend/src/test/...`
- [ ] T020 [P] [US2] Frontend/manual verification for `[user journey]` if UI changes

### Implementation for User Story 2

- [ ] T021 [P] [US2] Create/update backend DTO/domain pieces in the task package
- [ ] T022 [US2] Implement service and repository behavior
- [ ] T023 [US2] Implement REST endpoint/controller behavior
- [ ] T024 [US2] Integrate frontend API client and UI pieces if needed

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3

- [ ] T025 [P] [US3] Backend API verification for `[endpoint]` in `backend/src/test/...`
- [ ] T026 [P] [US3] Frontend/manual verification for `[user journey]` if UI changes

### Implementation for User Story 3

- [ ] T027 [P] [US3] Create/update backend DTO/domain pieces in the task package
- [ ] T028 [US3] Implement service and repository behavior
- [ ] T029 [US3] Implement REST endpoint/controller behavior
- [ ] T030 [US3] Integrate frontend API client and UI pieces if needed

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional backend tests in `backend/src/test/`
- [ ] TXXX [P] Additional frontend checks in `frontend/` if UI changed
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Domain/DTOs before repositories and services
- Services before controllers/endpoints
- Backend API verification before frontend integration
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all verification for User Story 1 together:
Task: "Backend API verification for [endpoint] in backend/src/test/..."
Task: "Frontend/manual verification for [user journey] if UI changes"

# Launch independent backend model/DTO work:
Task: "Create/update domain model in backend/src/main/java/com/example/taskapp/task/domain/"
Task: "Create/update DTOs in backend/src/main/java/com/example/taskapp/task/dto/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
