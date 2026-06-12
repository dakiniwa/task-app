# タスク: フロントエンド タスク基本管理

**入力**: `/specs/002-frontend-task-management/` の設計ドキュメント
**前提**: [plan.md](./plan.md), [spec.md](./spec.md), [research.md](./research.md), [data-model.md](./data-model.md), [contracts/](./contracts/), [quickstart.md](./quickstart.md)
**方針**: SCRUM-7 全体の tasks.md として作成し、実行単位は Jira サブタスク別にグルーピングする。

**テスト**: 自動テストランナーは未導入のため、`npm run lint`、`npm run build`、`quickstart.md` の手動受け入れシナリオを確認タスクとして含める。

## 形式: `[ID] [P?] [Story] 説明`

- **[P]**: 並列実行可能。別ファイルで、未完了タスクへの依存がない。
- **[Story]**: 対象ユーザーストーリー。例: `[US1]`, `[US2]`。
- 説明には正確なファイルパスを含める。

## Phase 1: セットアップ (Jira: SCRUM-25)

**目的**: フロントエンド開発・確認基盤と最小ディレクトリ構成を整える。

- [X] T001 `AGENTS.md` と `frontend/AGENTS.md` を確認し、SCRUM-7 実装で守るルールを `specs/002-frontend-task-management/quickstart.md` の確認観点として把握する
- [X] T002 [P] `frontend/.env.example` に `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080` を追加する
- [X] T003 `frontend/src/features/tasks/` と `frontend/src/shared/` の構成を plan.md に合わせて作成する
- [X] T004 [P] `frontend/src/shared/lib/apiConfig.ts` に `NEXT_PUBLIC_API_BASE_URL` を読む API base URL ヘルパーを作成する
- [X] T005 `frontend/package.json` と `frontend/package-lock.json` の `typescript` を 6 系へ更新し、`frontend/tsconfig.json` の `target`, `lib`, `noEmit`, `moduleResolution` の役割を確認したうえで、`frontend/package.json` の `lint` と `build` を確認コマンドとして実行できることを確認する

**チェックポイント**: SCRUM-25 完了時点で、後続フェーズが `features/tasks` と `shared` に実装を追加できる。

---

## Phase 2: 基盤 (Jira: SCRUM-26)

**目的**: API連携、型定義、共通UI状態管理の土台を整える。

**重要**: このフェーズが完了するまで、US1〜US5 の画面実装を開始しない。

- [ ] T006 `frontend/src/features/tasks/types/taskDto.ts` に `TaskStatus`, `TaskResponse`, `TaskCreateRequest`, `TaskUpdateRequest`, `ErrorResponse`, `ErrorDetail` を定義する
- [ ] T007 `frontend/src/features/tasks/types/taskUi.ts` に `Task`, `TaskFormState`, `TaskOverlayState`, `OperationFeedback`, `UserContext` を定義する
- [ ] T008 [P] `frontend/src/shared/lib/httpError.ts` に HTTP status 別の UI エラー種別を定義する
- [ ] T009 `frontend/src/features/tasks/api/taskApiError.ts` に `ErrorResponse` を UI 表示用エラーへ変換する処理を実装する
- [ ] T010 `frontend/src/features/tasks/api/taskApi.ts` に `listTasks`, `createTask`, `getTask`, `updateTask`, `deleteTask` を標準 `fetch` で実装する
- [ ] T011 `frontend/src/features/tasks/hooks/useTasks.ts` に userId、tasks、selectedTask、overlay mode、loading、success、error の状態管理を実装する
- [ ] T012 [P] `frontend/src/shared/components/LoadingIndicator.tsx` に共通 loading 表示コンポーネントを作成する
- [ ] T013 [P] `frontend/src/shared/components/FeedbackMessage.tsx` に success/error 表示コンポーネントを作成する
- [ ] T014 `frontend/src/features/tasks/hooks/useTaskForm.ts` に title/status 必須検証と `ErrorResponse.details` の fieldErrors 変換を実装する
- [ ] T015 `frontend/package.json` の `npm run lint` を実行し、SCRUM-26 基盤の静的確認を行う

**チェックポイント**: API DTO と UI 状態が分離され、画面実装が API クライアントと hooks を利用できる。

---

## Phase 3: ユーザーストーリー 1 - 画面からタスクを登録する (Priority: P1 / Jira: SCRUM-27) MVP

**Goal**: ユーザーが一覧画面から新規登録 TaskOverlay を開き、タイトル・説明・ステータスを入力してタスクを登録できる。

**独立テスト**: `specs/002-frontend-task-management/quickstart.md` の「2. 新規登録」を実行し、登録成功、必須入力エラー、loading、API失敗表示を確認する。

### 実装

- [ ] T016 [P] [US1] `frontend/src/features/tasks/components/TaskCreateForm.tsx` に title、description、status の登録フォームを実装する
- [ ] T017 [US1] `frontend/src/features/tasks/hooks/useTaskForm.ts` に登録フォームの初期値、送信前検証、fieldErrors 表示用の戻り値を追加する
- [ ] T018 [US1] `frontend/src/features/tasks/hooks/useTasks.ts` に `createTask` 呼び出し、登録成功通知、登録後の一覧反映処理を追加する
- [ ] T019 [US1] `frontend/src/features/tasks/components/TaskOverlay.tsx` に create mode の右ドロワー/スマホ全画面モーダル表示を実装する
- [ ] T020 [US1] `frontend/src/app/page.tsx` に新規登録ボタンと create mode の TaskOverlay 起動を接続する
- [ ] T021 [US1] `specs/002-frontend-task-management/quickstart.md` の新規登録シナリオに従って、登録成功、必須入力エラー、API失敗を手動確認する

**チェックポイント**: US1 単体で、タスク登録と登録後の一覧反映をデモできる。

---

## Phase 4: ユーザーストーリー 2 - 自分のタスクを一覧と詳細で確認する (Priority: P2 / Jira: SCRUM-28)

**Goal**: ユーザーが userId 単位のタスク一覧を確認し、選択したタスクの詳細を右ドロワー/スマホ全画面モーダルで確認できる。

**独立テスト**: `specs/002-frontend-task-management/quickstart.md` の「1. userId と一覧」「3. 詳細」を実行し、userId 切り替え、一覧、空状態、詳細表示、status 表示を確認する。

### 実装

- [ ] T022 [P] [US2] `frontend/src/features/tasks/components/TaskStatusBadge.tsx` に `TODO`, `DOING`, `DONE` の status badge 表示を実装する
- [ ] T023 [P] [US2] `frontend/src/features/tasks/components/TaskList.tsx` にタスク一覧、空状態、タスク選択導線を実装する
- [ ] T024 [US2] `frontend/src/features/tasks/components/TaskDetailPanel.tsx` にタイトル、説明、ステータスを表示する詳細表示を実装する
- [ ] T025 [US2] `frontend/src/features/tasks/hooks/useTasks.ts` に `listTasks`, `getTask`, userId 切り替え、selectedTask 更新処理を追加する
- [ ] T026 [US2] `frontend/src/features/tasks/components/TaskOverlay.tsx` に detail mode の右ドロワー/スマホ全画面モーダル表示を実装する
- [ ] T027 [US2] `frontend/src/app/page.tsx` に userId 入力、TaskList、TaskDetailPanel、loading/error 表示を接続する
- [ ] T028 [US2] `specs/002-frontend-task-management/quickstart.md` の一覧・詳細シナリオに従って、userId 切り替え、空状態、詳細表示、status 表示を手動確認する

**チェックポイント**: US2 単体で、現在の userId のタスク一覧と詳細を確認できる。

---

## Phase 5: ユーザーストーリー 3 - タスクの内容と進捗を編集する (Priority: P3 / Jira: SCRUM-29)

**Goal**: ユーザーが詳細表示から編集へ進み、タイトル・説明・ステータスを更新できる。

**独立テスト**: `specs/002-frontend-task-management/quickstart.md` の「4. 編集」を実行し、更新成功、必須入力エラー、loading、API失敗表示を確認する。

### 実装

- [ ] T029 [P] [US3] `frontend/src/features/tasks/components/TaskEditForm.tsx` に既存タスクの title、description、status を編集するフォームを実装する
- [ ] T030 [US3] `frontend/src/features/tasks/hooks/useTaskForm.ts` に既存タスクから編集フォーム初期値を作る処理を追加する
- [ ] T031 [US3] `frontend/src/features/tasks/hooks/useTasks.ts` に `updateTask` 呼び出し、更新成功通知、一覧と詳細への反映処理を追加する
- [ ] T032 [US3] `frontend/src/features/tasks/components/TaskDetailPanel.tsx` に編集モードへ進む操作を追加する
- [ ] T033 [US3] `frontend/src/features/tasks/components/TaskOverlay.tsx` に edit mode の右ドロワー/スマホ全画面モーダル表示を実装する
- [ ] T034 [US3] `specs/002-frontend-task-management/quickstart.md` の編集シナリオに従って、更新成功、必須入力エラー、API失敗を手動確認する

**チェックポイント**: US3 単体で、既存タスクの内容と status を更新できる。

---

## Phase 6: ユーザーストーリー 4 - 不要なタスクを削除する (Priority: P4 / Jira: SCRUM-30)

**Goal**: ユーザーが詳細表示から削除確認を経てタスクを削除できる。

**独立テスト**: `specs/002-frontend-task-management/quickstart.md` の「5. 削除」を実行し、キャンセル、削除成功、対象なし、loading、API失敗表示を確認する。

### 実装

- [ ] T035 [P] [US4] `frontend/src/features/tasks/components/TaskDeleteConfirm.tsx` に削除確認 UI を実装する
- [ ] T036 [US4] `frontend/src/features/tasks/hooks/useTasks.ts` に `deleteTask` 呼び出し、削除成功通知、一覧と selectedTask からの除外処理を追加する
- [ ] T037 [US4] `frontend/src/features/tasks/components/TaskDetailPanel.tsx` に削除確認へ進む操作を追加する
- [ ] T038 [US4] `frontend/src/features/tasks/components/TaskOverlay.tsx` に deleteConfirm mode の右ドロワー/スマホ全画面モーダル表示を実装する
- [ ] T039 [US4] `specs/002-frontend-task-management/quickstart.md` の削除シナリオに従って、キャンセル、削除成功、404 エラー表示を手動確認する

**チェックポイント**: US4 単体で、削除前確認と論理削除 API 呼び出しを確認できる。

---

## Phase 7: ユーザーストーリー 5 - 操作状態とエラーを理解して使い続ける (Priority: P5 / Jira: SCRUM-31)

**Goal**: ユーザーが登録・一覧・詳細・編集・削除の loading、success、error を理解でき、失敗後も操作を続けられる。

**独立テスト**: `specs/002-frontend-task-management/quickstart.md` の「6. エラー」を実行し、backend 停止時、404、400、500 系の表示と操作継続を確認する。

### 実装

- [ ] T040 [US5] `frontend/src/shared/components/LoadingIndicator.tsx` を一覧取得、登録、更新、削除の処理中表示に適用する
- [ ] T041 [US5] `frontend/src/shared/components/FeedbackMessage.tsx` を登録、更新、削除の success/error 表示に適用する
- [ ] T042 [US5] `frontend/src/features/tasks/api/taskApiError.ts` に `400`, `404`, `500` 系、その他通信失敗の表示メッセージ変換を整理する
- [ ] T043 [US5] `frontend/src/features/tasks/hooks/useTaskForm.ts` に `ErrorResponse.details` を入力欄の fieldErrors へ紐付ける処理を完成させる
- [ ] T044 [US5] `frontend/src/app/page.tsx` でエラー発生後も userId 切り替え、再読み込み、新規登録を継続できる状態にする
- [ ] T045 [US5] `specs/002-frontend-task-management/quickstart.md` のエラーシナリオに従って、API失敗後に画面がクラッシュせず操作継続できることを手動確認する

**チェックポイント**: US5 単体で、主要操作の状態表示と失敗後の操作継続を確認できる。

---

## Phase 8: 全体確認 (Jira: SCRUM-32)

**目的**: SCRUM-7 全体の API 連携、品質制約、完了条件を確認する。

- [ ] T046 `frontend/package.json` の `npm run lint` を実行し、frontend の lint が成功することを確認する
- [ ] T047 `frontend/package.json` の `npm run build` を実行し、frontend の build が成功することを確認する
- [ ] T048 `specs/002-frontend-task-management/contracts/task-api-client.md` と `frontend/src/features/tasks/api/taskApi.ts` を照合し、HTTP method、path、request/response が一致していることを確認する
- [ ] T049 `specs/002-frontend-task-management/contracts/task-ui.md` と `frontend/src/app/page.tsx` を照合し、デスクトップ右ドロワーとスマホ全画面モーダルの表示を手動確認する
- [ ] T050 `specs/002-frontend-task-management/quickstart.md` の登録から削除までの一連シナリオを実行し、結果を確認する

---

## Phase 9: 仕上げと保守性改善 (Jira: SCRUM-33)

**目的**: フォーム、エラー表示、状態管理の重複を必要最小限で整理し、今後の機能追加に備える。

- [ ] T051 `frontend/src/features/tasks/components/TaskCreateForm.tsx` と `frontend/src/features/tasks/components/TaskEditForm.tsx` の重複を確認し、必要最小限の共通化を行う
- [ ] T052 `frontend/src/features/tasks/hooks/useTasks.ts` の loading、success、error、selectedTask、overlay mode の責務を整理する
- [ ] T053 `frontend/src/features/tasks/types/taskDto.ts` と `frontend/src/features/tasks/types/taskUi.ts` を確認し、API DTO と UI 状態が混在していないことを確認する
- [ ] T054 `frontend/src/features/tasks/api/taskApi.ts` を確認し、画面コンポーネント側で URL 文字列を組み立てていないことを確認する
- [ ] T055 `specs/002-frontend-task-management/quickstart.md` の主要シナリオを再実行し、登録・一覧・詳細・編集・削除が退行していないことを確認する

---

## 依存関係と実行順序

### Jira サブタスク順序

1. **SCRUM-25**: セットアップ基盤
2. **SCRUM-26**: API連携・型定義・共通UI状態基盤
3. **SCRUM-27**: US1 登録
4. **SCRUM-28**: US2 一覧・詳細
5. **SCRUM-29**: US3 編集
6. **SCRUM-30**: US4 削除
7. **SCRUM-31**: US5 操作状態・エラー
8. **SCRUM-32**: 全体確認
9. **SCRUM-33**: 保守性改善

### フェーズ依存関係

- **Phase 1**: 依存なし。最初に実施する。
- **Phase 2**: Phase 1 完了に依存し、すべてのユーザーストーリーをブロックする。
- **Phase 3 (US1)**: Phase 2 完了後に開始する。MVP。
- **Phase 4 (US2)**: Phase 2 完了後に開始できるが、登録後反映の確認は US1 と統合すると学習しやすい。
- **Phase 5 (US3)**: US2 の詳細表示に依存する。
- **Phase 6 (US4)**: US2 の詳細表示に依存する。
- **Phase 7 (US5)**: US1〜US4 の主要操作が揃った後に横断適用する。
- **Phase 8/9**: US1〜US5 完了後に実施する。

### 並列化できる機会

- T002 と T004 は別ファイルのため並列実行できる。
- T008、T012、T013 は別ファイルのため、DTO 定義と並行して下準備できる。
- T022 と T023 は別コンポーネントのため並列実行できる。
- T029 と T035 は別ストーリー・別コンポーネントだが、実運用では US2 の詳細表示完了後に開始する。

---

## 並列実行例

### SCRUM-26 基盤

```bash
Task: "frontend/src/features/tasks/types/taskDto.ts に API DTO を定義する"
Task: "frontend/src/shared/lib/httpError.ts に HTTP status 別の UI エラー種別を定義する"
Task: "frontend/src/shared/components/LoadingIndicator.tsx に共通 loading 表示コンポーネントを作成する"
Task: "frontend/src/shared/components/FeedbackMessage.tsx に success/error 表示コンポーネントを作成する"
```

### SCRUM-28 一覧・詳細

```bash
Task: "frontend/src/features/tasks/components/TaskStatusBadge.tsx に status badge 表示を実装する"
Task: "frontend/src/features/tasks/components/TaskList.tsx に一覧と空状態を実装する"
```

---

## 実装戦略

### MVP First

1. Phase 1 (SCRUM-25) を完了する。
2. Phase 2 (SCRUM-26) を完了する。
3. Phase 3 (SCRUM-27 / US1) を完了する。
4. `quickstart.md` の新規登録シナリオで停止して検証する。

### インクリメンタルデリバリー

1. SCRUM-25/26 で基盤を固める。
2. SCRUM-27 で登録を実装し、一覧反映まで確認する。
3. SCRUM-28 で一覧・詳細を実装し、userId スコープを確認する。
4. SCRUM-29 で編集、SCRUM-30 で削除を追加する。
5. SCRUM-31〜33 で状態表示、全体確認、保守性改善を行う。

### ブランチ運用

- 各 Jira サブタスクは `feature/sub/SCRUM-x` で作業する。
- サブタスク完了時は `feature/sub/SCRUM-x` から `feature/SCRUM-7` へ Pull Request を作成する。
- SCRUM-7 全体完了後に `feature/SCRUM-7` から `main` へ Pull Request を作成する。
