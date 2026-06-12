# 実装計画: フロントエンド タスク基本管理

**Branch**: `feature/SCRUM-7` | **日付**: 2026-06-12 | **仕様**: [spec.md](./spec.md)
**入力**: `/specs/002-frontend-task-management/spec.md` の機能仕様

## 概要

SCRUM-7 では、SCRUM-6 で定義済みのタスク基本管理 API に接続するフロントエンドを実装する。UI はタスク一覧を主画面とし、新規登録・詳細・編集・削除確認をデスクトップでは右ドロワー、スマホでは全画面モーダルで扱う。

計画は学習しながら段階的に進められるように、最初に SCRUM-25 で Next.js / TypeScript の確認基盤と最小ディレクトリを整え、次に SCRUM-26 で Task 型、APIクライアント、loading/success/error、userId 状態の土台を作る。その後、登録、一覧・詳細、編集、削除、統合確認の順に画面機能を積み上げる。

## 技術コンテキスト

**言語/バージョン**: TypeScript 6、Next.js 16.2.4、React 19.2.4
**主要依存関係**: Next.js App Router、React Hooks、Tailwind CSS 4、標準 `fetch`
**ストレージ**: フロントエンド側の永続ストレージは使用しない。タスクデータは SCRUM-6 の backend API を正とする。
**テスト**: 現時点の frontend scripts は `npm run lint` と `npm run build`。自動テストランナーは未導入のため、この計画では追加せず、手動受け入れ確認を quickstart に定義する。
**対象プラットフォーム**: ブラウザ向け Web アプリケーション。ローカル開発では frontend と backend を別プロセスで起動する。
**プロジェクト種別**: `web-application`
**性能目標**: 処理中状態は操作開始から1秒以内に認識できる。初回利用者が userId 指定から登録・一覧確認まで2分以内に完了できる。
**制約**: status は `TODO`, `DOING`, `DONE` のみ。認証は導入しない。API base URL は `NEXT_PUBLIC_API_BASE_URL` で管理する。API DTO と UI 状態を分離する。TypeScript は `frontend/package.json` と `frontend/package-lock.json` で 6 系へ揃える。`frontend/tsconfig.json` の `target` は TypeScript の型チェック時に想定する JavaScript 構文レベルであり、変更する場合は SCRUM-25 でブラウザ対応方針と合わせて確認する。その他の新規依存は現在必要になるまで追加しない。
**スケール/スコープ**: 1つのタスク管理画面、5つの API 操作、3つの Task status、1つの userId 入力/切り替え、右ドロワー/全画面モーダルによる TaskOverlay。

## 憲章チェック

*GATE: Phase 0 research の前に合格必須。Phase 1 design 後に再確認する。*

- **仕様参照**: 合格。`.specify/memory/constitution.md`、ルート `AGENTS.md`、`frontend/AGENTS.md`、`specs/002-frontend-task-management/spec.md`、SCRUM-6 API 契約、Jira SCRUM-25/26 を確認済み。
- **バックエンドAPI先行**: 合格。新規 backend API は作らず、SCRUM-6 の `/users/{userId}/tasks` と `/users/{userId}/tasks/{taskId}` を利用する。
- **REST/user scope**: 合格。既存 API は userId をパスに含む複数形リソースで、URL に動詞を含めない。
- **ドメイン不変条件**: 合格。フロントエンドは `TODO` / `DOING` / `DONE` のみを表示・選択し、削除は backend の論理削除 API 呼び出しとして扱う。
- **境界分離**: 合格。`features/tasks/api` に API DTO と通信処理、`features/tasks/types` に UI 状態型、`features/tasks/hooks` に画面状態を分離する。
- **バリデーションと観測性**: 合格。送信前の必須入力検証、HTTP status 別エラー表示、loading/success/error 表示、手動受け入れ確認を計画に含める。
- **日本語成果物**: 合格。Spec Kit 成果物は日本語で作成する。
- **Jiraブランチ運用**: 合格。親は `feature/SCRUM-7`。サブタスク実装は `feature/sub/SCRUM-25` などから親ブランチへ PR を作成する。

## プロジェクト構成

### ドキュメント (この機能)

```text
specs/002-frontend-task-management/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── task-api-client.md
│   └── task-ui.md
└── tasks.md
```

### ソースコード (リポジトリルート)

```text
frontend/
├── src/
│   ├── app/
│   │   ├── globals.css
│   │   ├── layout.tsx
│   │   └── page.tsx
│   ├── features/
│   │   └── tasks/
│   │       ├── api/
│   │       │   ├── taskApi.ts
│   │       │   └── taskApiError.ts
│   │       ├── components/
│   │       │   ├── TaskCreateForm.tsx
│   │       │   ├── TaskDetailPanel.tsx
│   │       │   ├── TaskList.tsx
│   │       │   ├── TaskOverlay.tsx
│   │       │   └── TaskStatusBadge.tsx
│   │       ├── hooks/
│   │       │   ├── useTaskForm.ts
│   │       │   └── useTasks.ts
│   │       └── types/
│   │           ├── taskDto.ts
│   │           └── taskUi.ts
│   └── shared/
│       ├── components/
│       │   ├── FeedbackMessage.tsx
│       │   └── LoadingIndicator.tsx
│       └── lib/
│           ├── apiConfig.ts
│           └── httpError.ts
└── package.json
```

**構成判断**: `frontend/AGENTS.md` の標準構成に合わせ、タスク機能は `src/features/tasks` に閉じる。API 通信は `features/tasks/api`、API DTO は `features/tasks/types/taskDto.ts`、UI 状態は `features/tasks/types/taskUi.ts` に分離する。共通の loading/error 表示と API base URL は `shared` に置く。現状の `frontend/src/app/page.tsx` は初期画面の入口として使い、実装フェーズで一覧中心 UI へ接続する。

## フェーズ計画

### SCRUM-25: フロントエンドセットアップ基盤

- `frontend/AGENTS.md` と既存構成を確認し、作業ルールを実装単位へ反映する。
- `frontend/package.json` と `frontend/package-lock.json` の `typescript` を 6 系へ更新し、ローカルの実解決バージョンを確認する。
- `frontend/tsconfig.json` の `target`, `lib`, `noEmit`, `moduleResolution` を確認し、`target: ES2017` が Next.js テンプレート由来の互換性設定であることを把握する。変更する場合は、対応ブラウザ方針と lint/build 結果を確認してから行う。
- `npm run lint` と `npm run build` が確認コマンドであることを明確にする。
- `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080` を API 接続先として扱う。
- `src/features/tasks` と `src/shared` の最小ディレクトリを作れる状態にする。
- 既存のボード風 `page.tsx` はプロトタイプとみなし、仕様で確定した一覧中心 UI へ置き換える前提を共有する。

### SCRUM-26: API連携・型定義・共通UI状態基盤

- `TaskResponse`, `TaskCreateRequest`, `TaskUpdateRequest`, `TaskStatus`, `ErrorResponse`, `ErrorDetail` を backend 契約に合わせて定義する。
- `Task` などの UI 状態型を API DTO と分けて定義する。
- `listTasks`, `createTask`, `getTask`, `updateTask`, `deleteTask` の API クライアントを `features/tasks/api` に集約する。
- `400`, `404`, `500` 系のエラーを `frontend/AGENTS.md` の方針に従って表示できる形へ変換する。
- userId、loading、success、error、selectedTask、overlay mode を React Hooks で扱い、グローバル状態は導入しない。

### 後続フェーズ

- SCRUM-27: 新規登録用 TaskOverlay と送信前バリデーション。
- SCRUM-28: userId 切り替え、一覧表示、詳細表示、右ドロワー/スマホ全画面モーダル。
- SCRUM-29: 詳細表示内の編集モードと更新処理。
- SCRUM-30: 削除確認と論理削除 API 呼び出し。
- SCRUM-31/32: API 結合、操作シナリオ、レスポンシブ、エラー表示、lint/build 確認。
- SCRUM-33: フォーム、エラー表示、hooks、コンポーネント分割の保守性改善。

## Phase 0: 調査結果

調査結果は [research.md](./research.md) に記録した。未解決事項は残さない。

## Phase 1: 設計成果物

- データモデル: [data-model.md](./data-model.md)
- API クライアント契約: [contracts/task-api-client.md](./contracts/task-api-client.md)
- UI 契約: [contracts/task-ui.md](./contracts/task-ui.md)
- クイックスタート: [quickstart.md](./quickstart.md)

## 憲章チェック (設計後再確認)

- **シンプルさ最優先**: 合格。TypeScript は 6 系へ更新するが、追加ライブラリやグローバル状態は導入せず、React Hooks と標準 `fetch` を前提にする。`tsconfig.json` の `target` は目的を明確化し、必要性が確認できた場合のみ変更する。
- **一貫性**: 合格。`frontend/AGENTS.md` の `features/tasks` 構成、環境変数、DTO/UI状態分離、エラー方針に合わせた。
- **バックエンドAPI先行**: 合格。SCRUM-6 の OpenAPI 契約を参照し、フロントエンドで backend 責務を再定義しない。
- **仕様を唯一の正とする**: 合格。UI は `spec.md` の Clarifications に従い、一覧中心 + デスクトップ右ドロワー + スマホ全画面モーダルに固定した。
- **境界の明確化と拡張性**: 合格。API DTO、UI 型、hooks、components、shared 表示部品を責務で分け、必要最小限の拡張余地を残す。

## 複雑性の追跡

憲章チェック違反はない。
