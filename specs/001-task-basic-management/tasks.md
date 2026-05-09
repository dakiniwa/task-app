# タスク: タスク基本管理

**入力**: `/specs/001-task-basic-management/` の設計ドキュメント
**前提**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml, quickstart.md
**テスト**: FR-014 により、登録、一覧、詳細、更新、削除、バリデーション、共通エラー、JST 時刻表現の backend 単体テストと統合テストを含める。
**対象**: backend REST API のみ。frontend 接続は今回の実装対象に含めない。

## テスト方針と命名規約

- **単体テスト**: product class ごとに `プロダクトクラス名 + Test` として作成する。例: `TaskServiceTest`、`JstDateTimeFormatterTest`、`GlobalExceptionHandlerTest`。
- **統合テスト**: Spring context、MockMvc、JPA、PostgreSQL Testcontainers など複数境界を使う検証は `XXXIntegrationTest` として作成する。例: `TaskCreateIntegrationTest`、`TaskRepositoryIntegrationTest`。
- 各ユーザーストーリーは、Service などの単体テストと API/DB 境界を含む統合テストの両方で独立検証できるようにする。

## 形式: `[ID] [P?] [Story] 説明`

- **[P]**: 別ファイルで作業でき、未完了タスクに依存しない並列実行可能タスク。
- **[Story]**: 対象ユーザーストーリー。Setup、基盤、仕上げには付けない。
- すべてのタスク説明には具体的なファイルパスを含める。

---

## Phase 1: セットアップ (共有基盤)

**目的**: backend API 実装に必要な依存関係、DB 設定、時刻基盤を整える。

- [ ] T001 `backend/pom.xml` に Spring Data JPA、Spring Validation、PostgreSQL JDBC Driver、PostgreSQL Testcontainers、JUnit Jupiter Testcontainers 依存関係を追加する
- [ ] T002 `backend/src/main/resources/application.yaml` に環境変数参照の PostgreSQL datasource と JPA 設定を追加する
- [ ] T003 [P] `backend/src/test/resources/application-test.yaml` に backend テスト用 profile 設定を追加する
- [ ] T004 [P] `backend/src/main/java/com/example/taskapp/common/config/TimeConfig.java` に `ZoneId.of("Asia/Tokyo")` と `Clock` Bean を定義する

---

## Phase 2: 基盤 (ブロッキング前提)

**目的**: すべてのユーザーストーリーに必要なドメイン、永続化、共通エラー、時刻変換の基盤を作る。

**重要**: このフェーズが完了するまで、ユーザーストーリー作業を開始しない。

- [ ] T005 [P] `backend/src/main/java/com/example/taskapp/task/domain/TaskStatus.java` に `TODO`、`DOING`、`DONE` のみを持つ enum を作成する
- [ ] T006 `backend/src/main/java/com/example/taskapp/task/domain/Task.java` に `id`、`userId`、`title`、`description`、`status`、`deleted`、`createdAt`、`updatedAt` を持つ JPA Entity を作成する
- [ ] T007 [P] `backend/src/main/java/com/example/taskapp/common/exception/ErrorDetail.java` に共通エラー詳細 DTO を作成する
- [ ] T008 [P] `backend/src/main/java/com/example/taskapp/common/exception/ErrorResponse.java` に `code`、`message`、`details` を持つ共通エラー DTO を作成する
- [ ] T009 [P] `backend/src/main/java/com/example/taskapp/common/exception/TaskNotFoundException.java` に task 未検出用例外を作成する
- [ ] T010 `backend/src/main/java/com/example/taskapp/task/repository/TaskRepository.java` に `findByIdAndUserIdAndDeletedFalse` と `findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc` を持つ Spring Data JPA repository を作成する
- [ ] T011 `backend/src/main/java/com/example/taskapp/common/exception/GlobalExceptionHandler.java` に validation、JSON parse、`TaskNotFoundException`、想定外例外の共通ハンドリングを実装する
- [ ] T012 [P] `backend/src/main/java/com/example/taskapp/common/util/JstDateTimeFormatter.java` に `Instant` を JST オフセット付き ISO-8601 文字列へ変換する utility を作成する
- [ ] T013 [P] `backend/src/test/java/com/example/taskapp/common/util/JstDateTimeFormatterTest.java` に JST オフセット付き ISO-8601 変換の単体テストを追加する
- [ ] T014 [P] `backend/src/test/java/com/example/taskapp/task/repository/TaskRepositoryIntegrationTest.java` に PostgreSQL Testcontainers を使った userId scope と logical delete の統合テストを追加する
- [ ] T015 [P] `backend/src/test/java/com/example/taskapp/common/exception/GlobalExceptionHandlerTest.java` に 400、404、500 が `ErrorResponse` 形式になる共通例外ハンドリングの単体テストを追加する

**チェックポイント**: Task の永続化、userId scope、論理削除、共通エラー、JST 変換の土台ができ、各ストーリーを実装できる。

---

## Phase 3: ユーザーストーリー 1 - 自分のタスクを登録する (Priority: P1) MVP

**Goal**: 個人ユーザーが `POST /users/{userId}/tasks` でタイトル、任意の説明、ステータスを登録できる。

**独立テスト**: `userId` と有効な request body で登録すると 201 と登録内容が返り、不正 status や必須項目不足では `ErrorResponse` 形式の 400 が返ることを確認する。

### ユーザーストーリー 1 のテスト

- [ ] T016 [P] [US1] `backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java` に固定 `Clock` と mock repository を使った `createTask` の単体テストを追加する
- [ ] T017 [P] [US1] `backend/src/test/java/com/example/taskapp/task/controller/TaskCreateIntegrationTest.java` に POST 正常系、userId 空文字・空白のみ、不正 status、必須項目不足、JST timestamp の MockMvc 統合テストを追加する

### ユーザーストーリー 1 の実装

- [ ] T018 [P] [US1] `backend/src/main/java/com/example/taskapp/task/dto/TaskCreateRequest.java` に `title` の `@NotBlank` 相当 validation、`status` の validation、任意 `description` を持つ登録 request DTO を作成する
- [ ] T019 [P] [US1] `backend/src/main/java/com/example/taskapp/task/dto/TaskResponse.java` に `id`、`userId`、`title`、`description`、`status`、`createdAt`、`updatedAt` を持つ response DTO を作成する
- [ ] T020 [US1] `backend/src/main/java/com/example/taskapp/task/dto/TaskResponseMapper.java` に `Task` から `TaskResponse` へ JST timestamp 付きで変換する mapper を作成する
- [ ] T021 [US1] `backend/src/main/java/com/example/taskapp/task/service/TaskService.java` に `createTask(String userId, TaskCreateRequest request)` と transaction boundary を実装する
- [ ] T022 [US1] `backend/src/main/java/com/example/taskapp/task/controller/TaskController.java` に `@NotBlank` 相当の `userId` path validation を含む `POST /users/{userId}/tasks` を追加し、成功時に 201 Created と `TaskResponse` を返す

**チェックポイント**: ユーザーストーリー 1 が単体で動作し、MVP として登録 API を検証できる。

---

## Phase 4: ユーザーストーリー 2 - 自分のタスクを確認する (Priority: P2)

**Goal**: 個人ユーザーが `GET /users/{userId}/tasks` と `GET /users/{userId}/tasks/{taskId}` で自分の未削除タスクだけを確認できる。

**独立テスト**: 複数 userId と deleted task を test fixture で用意し、一覧が指定 userId の未削除 task のみを返し、詳細が一致し、存在しない task、別 userId、削除済み task は 404 になることを確認する。

### ユーザーストーリー 2 のテスト

- [ ] T023 [US2] `backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java` に `listTasks` と `getTask` の userId scope、deleted 除外、404 の単体テストを追加する
- [ ] T024 [P] [US2] `backend/src/test/java/com/example/taskapp/task/controller/TaskQueryIntegrationTest.java` に list/detail の正常系、userId 空文字・空白のみ、別 userId 除外、deleted 除外、404 の MockMvc 統合テストを追加する

### ユーザーストーリー 2 の実装

- [ ] T025 [US2] `backend/src/main/java/com/example/taskapp/task/service/TaskService.java` に `listTasks(String userId)` と `getTask(String userId, Long taskId)` を実装する
- [ ] T026 [US2] `backend/src/main/java/com/example/taskapp/task/controller/TaskController.java` に `@NotBlank` 相当の `userId` path validation を含む `GET /users/{userId}/tasks` と `GET /users/{userId}/tasks/{taskId}` を追加する

**チェックポイント**: ユーザーストーリー 2 が単体テストと統合テストで検証でき、US1 と組み合わせても登録後の一覧・詳細確認ができる。

---

## Phase 5: ユーザーストーリー 3 - タスクの内容と進捗を更新する (Priority: P3)

**Goal**: 個人ユーザーが `PUT /users/{userId}/tasks/{taskId}` で未削除タスクのタイトル、説明、ステータスを更新できる。

**独立テスト**: 登録済み task fixture を更新し、取得結果に更新内容と新しい `updatedAt` が反映され、不正 status、存在しない task、別 userId、削除済み task は共通エラーになることを確認する。

### ユーザーストーリー 3 のテスト

- [ ] T027 [US3] `backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java` に固定 `Clock` で `updateTask` が内容と `updatedAt` を更新する単体テストを追加する
- [ ] T028 [P] [US3] `backend/src/test/java/com/example/taskapp/task/controller/TaskUpdateIntegrationTest.java` に PUT 正常系、userId 空文字・空白のみ、必須項目不足、不正 status、404 の MockMvc 統合テストを追加する

### ユーザーストーリー 3 の実装

- [ ] T029 [P] [US3] `backend/src/main/java/com/example/taskapp/task/dto/TaskUpdateRequest.java` に `title` の `@NotBlank` 相当 validation、`status` の validation、任意 `description` を持つ更新 request DTO を作成する
- [ ] T030 [US3] `backend/src/main/java/com/example/taskapp/task/service/TaskService.java` に `updateTask(String userId, Long taskId, TaskUpdateRequest request)` を実装する
- [ ] T031 [US3] `backend/src/main/java/com/example/taskapp/task/controller/TaskController.java` に `@NotBlank` 相当の `userId` path validation を含む `PUT /users/{userId}/tasks/{taskId}` を追加する

**チェックポイント**: ユーザーストーリー 3 が単体テストと統合テストで検証でき、US1/US2 と組み合わせて登録、更新、確認の流れを検証できる。

---

## Phase 6: ユーザーストーリー 4 - 不要なタスクを取り除く (Priority: P4)

**Goal**: 個人ユーザーが `DELETE /users/{userId}/tasks/{taskId}` で task を論理削除し、通常の一覧・詳細から除外できる。

**独立テスト**: 登録済み task fixture を削除し、204 が返り、一覧と詳細で取得できず、存在しない task、別 userId、削除済み task は 404 になることを確認する。

### ユーザーストーリー 4 のテスト

- [ ] T032 [US4] `backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java` に `deleteTask` が `deleted=true` と `updatedAt` を更新する単体テストを追加する
- [ ] T033 [P] [US4] `backend/src/test/java/com/example/taskapp/task/controller/TaskDeleteIntegrationTest.java` に DELETE 正常系、userId 空文字・空白のみ、削除後取得不可、404 の MockMvc 統合テストを追加する

### ユーザーストーリー 4 の実装

- [ ] T034 [US4] `backend/src/main/java/com/example/taskapp/task/service/TaskService.java` に `deleteTask(String userId, Long taskId)` を実装し、物理削除ではなく `deleted=true` に更新する
- [ ] T035 [US4] `backend/src/main/java/com/example/taskapp/task/controller/TaskController.java` に `@NotBlank` 相当の `userId` path validation を含む `DELETE /users/{userId}/tasks/{taskId}` を追加し、成功時に 204 No Content を返す

**チェックポイント**: ユーザーストーリー 4 が単体テストと統合テストで検証でき、登録から論理削除までの主要操作が一通り成立する。

---

## Phase 7: 仕上げと横断関心事

**目的**: API 契約、quickstart、全体テスト、憲章制約を横断的に確認する。

- [ ] T036 [P] `backend/src/main/java/com/example/taskapp/task/controller/TaskController.java` の HTTP method、path、status code、schema が `specs/001-task-basic-management/contracts/openapi.yaml` に一致することを確認して Controller を修正する。契約変更が必要な場合は `specs/001-task-basic-management/spec.md` と `specs/001-task-basic-management/plan.md` へ戻って更新する
- [ ] T037 [P] `specs/001-task-basic-management/quickstart.md` の curl シナリオを実装後 API に合わせて検証し、差分があれば同ファイルを更新する
- [ ] T038 `backend/pom.xml` を基点に `cd backend && ./mvnw test` を実行し、`*Test` と `*IntegrationTest` の失敗があれば `backend/src/test/java/com/example/taskapp/` と `backend/src/main/java/com/example/taskapp/` の該当箇所を修正する
- [ ] T039 `backend/src/main/java/com/example/taskapp/` 全体で `ZoneId.systemDefault()` の未使用、独自エラーコード未追加、DTO/Entity 分離、URL に動詞がないことを確認して必要なら修正する

---

## 依存関係と実行順序

### フェーズ依存関係

- **Phase 1 セットアップ**: 依存なし。すぐ開始できる。
- **Phase 2 基盤**: Phase 1 完了に依存し、すべてのユーザーストーリーをブロックする。
- **Phase 3 US1**: Phase 2 完了後に開始する。MVP 範囲。
- **Phase 4 US2**: Phase 2 完了後に単体テストと統合テストで独立検証できる。インクリメンタル提供では US1 の後に実装する。
- **Phase 5 US3**: Phase 2 完了後に単体テストと統合テストで独立検証できる。インクリメンタル提供では US2 の後に実装する。
- **Phase 6 US4**: Phase 2 完了後に単体テストと統合テストで独立検証できる。インクリメンタル提供では US3 の後に実装する。
- **Phase 7 仕上げ**: 対象ユーザーストーリー完了後に実行する。

### ユーザーストーリー依存関係

- **US1 (P1)**: 登録 API。MVP。ほかのストーリーへの依存なし。
- **US2 (P2)**: 一覧・詳細 API。Service 単体テストと API/DB 統合テストで独立テスト可能。US1 と統合すると登録後確認の価値が完成する。
- **US3 (P3)**: 更新 API。Service 単体テストと API/DB 統合テストで独立テスト可能。US1/US2 と統合すると登録、更新、確認の流れが完成する。
- **US4 (P4)**: 論理削除 API。Service 単体テストと API/DB 統合テストで独立テスト可能。US2 と統合すると削除後の一覧・詳細除外が完成する。

### 各ユーザーストーリー内の順序

- 単体テストと統合テストのタスクを先に作成し、ビジネスロジックと API/DB の期待値を固定する。
- DTO と mapper を service/controller より前に作る。
- Service の transaction boundary と business rule を Controller endpoint より前に実装する。
- Controller は Service のみを呼び出し、Repository を直接呼ばない。
- 各ストーリー完了時に対象ストーリーの単体テストと統合テストを通す。

---

## 並列実行例

### ユーザーストーリー 1

```bash
Task: "T016 backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java"
Task: "T017 backend/src/test/java/com/example/taskapp/task/controller/TaskCreateIntegrationTest.java"
Task: "T018 backend/src/main/java/com/example/taskapp/task/dto/TaskCreateRequest.java"
Task: "T019 backend/src/main/java/com/example/taskapp/task/dto/TaskResponse.java"
```

### ユーザーストーリー 2

```bash
Task: "T023 backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java"
Task: "T024 backend/src/test/java/com/example/taskapp/task/controller/TaskQueryIntegrationTest.java"
```

### ユーザーストーリー 3

```bash
Task: "T027 backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java"
Task: "T028 backend/src/test/java/com/example/taskapp/task/controller/TaskUpdateIntegrationTest.java"
Task: "T029 backend/src/main/java/com/example/taskapp/task/dto/TaskUpdateRequest.java"
```

### ユーザーストーリー 4

```bash
Task: "T032 backend/src/test/java/com/example/taskapp/task/service/TaskServiceTest.java"
Task: "T033 backend/src/test/java/com/example/taskapp/task/controller/TaskDeleteIntegrationTest.java"
```

---

## 実装戦略

### MVP First (US1 のみ)

1. Phase 1 と Phase 2 を完了する。
2. Phase 3 の US1 を完了する。
3. `POST /users/{userId}/tasks` の正常系、入力不正、JST timestamp を単体テストと統合テストで確認する。
4. 登録 API を MVP として停止・レビューできる状態にする。

### インクリメンタルデリバリー

1. US1: 登録 API を追加し、MVP として検証する。
2. US2: 一覧・詳細 API を追加し、登録済み task の確認を可能にする。
3. US3: 更新 API を追加し、内容と進捗の変更を可能にする。
4. US4: 論理削除 API を追加し、不要 task の除外を可能にする。
5. Phase 7 で OpenAPI、quickstart、全体テスト、憲章制約を確認する。

### Jira ブランチ運用メモ

- 親 feature は `feature/SCRUM-6`。
- 各サブタスク実装開始直前に、対象 Jira issue に対応する `feature/sub/SCRUM-x` ブランチを切る。
- サブタスク完了時は `feature/sub/SCRUM-x` から `feature/SCRUM-6` へ Pull Request を作成する。

---

## Notes

- [P] タスクは別ファイルで進められるが、同一ストーリー内の Service/Controller 更新は順序を守る。
- `status` 未指定時の `TODO` 補完や部分更新は `backend/AGENTS.md` に記載があるが、今回の `specs/` 配下の仕様では status 必須、PUT による全体更新を優先する。
- `ErrorResponse.code` は HTTP ステータスコードの整数のみを使い、アプリケーション独自コードは追加しない。
- API 応答の `createdAt` / `updatedAt` は JST (`+09:00`) の ISO-8601 文字列とし、サーバのデフォルトタイムゾーンには依存しない。
