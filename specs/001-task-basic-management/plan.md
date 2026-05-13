# 実装計画: タスク基本管理

**Branch**: `feature/SCRUM-6` | **日付**: 2026-05-09 | **仕様**: [spec.md](./spec.md)
**入力**: `/specs/001-task-basic-management/spec.md` の機能仕様

**注記**: この計画は `/speckit-plan` の出力であり、Phase 2 のタスク生成は後続の `/speckit-tasks` で扱う。

## 概要

個人ユーザーが `userId` 単位でタスクを登録、一覧、詳細確認、更新、論理削除できるバックエンド REST API を先行して実装する。API は `/users/{userId}/tasks` と `/users/{userId}/tasks/{taskId}` に限定し、Task の status は `TODO`、`DOING`、`DONE` のみに制限する。存在しないタスク、別 `userId` のタスク、論理削除済みタスクは通常操作から除外し、共通エラーフォーマットで返す。

## 技術コンテキスト

**言語/バージョン**: Backend は Java 25、Spring Boot 4.0.6。Frontend は後続接続対象として Next.js 16.2.4、TypeScript 5。
**主要依存関係**: Spring Boot Starter Web、Spring Data JPA、Spring Validation、PostgreSQL JDBC Driver、Spring Boot Starter Test、DBUnit、H2 テスト datasource。
**ストレージ**: PostgreSQL 17。Task は JPA Entity として永続化し、削除は `deleted` フラグで表す。
**テスト**: Maven (`./mvnw test`)、JUnit 5、Spring Boot Test、MockMvc、JPA repository/service の統合テスト。Repository/JPA の既知データ投入は DBUnit で行い、test profile の H2 datasource を使う。
**対象プラットフォーム**: ローカル開発環境で稼働する Web API。既定ポートは Spring Boot の `8080`。
**プロジェクト種別**: モジュラモノリス内の backend web-service。frontend 連携は API 契約確定後の後続サイクル。
**性能目標**: 性能SLAは今回対象外。通常のローカル/単一DB構成でCRUDが成立することを確認する。
**制約**: 認証は導入せず、`userId` は外部から与えられる識別子として扱う。URL に動詞を含めない。`userId`、`title`、`status` は必須、`description` は任意。アプリケーション独自のエラーコードは追加しない。作成・更新時刻は JST (`Asia/Tokyo`, UTC+09:00) の API 表現に統一し、`ZoneId.systemDefault()` などサーバのデフォルトタイムゾーンには依存しない。
**スケール/スコープ**: Backend API 5 endpoints、主要 Entity は `Task`、Enum は `TaskStatus`、共通レスポンスは `ErrorResponse`。対象サブタスクは `SCRUM-16` から `SCRUM-23`。

## 憲章チェック

*GATE: Phase 0 research の前に合格必須。Phase 1 design 後に再確認する。*

- **仕様参照**: PASS。`.specify/memory/constitution.md`、ルート `AGENTS.md`、`backend/AGENTS.md`、`frontend/AGENTS.md`、[spec.md](./spec.md) を確認した。仕様判断は `specs/` 配下の機能仕様成果物を優先する。
- **バックエンドAPI先行**: PASS。本計画は backend REST API、永続化、DTO、共通エラー、API検証を対象とし、frontend 接続は後続に残す。
- **REST/user scope**: PASS。対象 API は `GET/POST /users/{userId}/tasks` と `GET/PUT/DELETE /users/{userId}/tasks/{taskId}`。動詞パスは使わない。
- **ドメイン不変条件**: PASS。Task status は `TODO` / `DOING` / `DONE` のみ。削除は論理削除。Task は `userId`、`createdAt`、`updatedAt` を保持する。API 応答の timestamp は JST オフセット付き ISO-8601 とする。
- **境界分離**: PASS。Entity/Enum は `domain`、DTO は `dto`、HTTP は `controller`、業務ロジックは `service`、DBアクセスは `repository` に分離する。
- **バリデーションと観測性**: PASS。DTO の入力検証、Service の所有者/削除状態検証、共通例外ハンドリング、主要 4xx/5xx のログ方針、正常系/異常系テストを計画に含める。
- **日本語成果物**: PASS。`plan.md`、`research.md`、`data-model.md`、`quickstart.md`、`contracts/` 配下の説明は日本語で作成する。
- **Jiraブランチ運用**: PASS。この計画は親ブランチ `feature/SCRUM-6` 上で作成する。サブタスク実装は `/speckit-tasks` 後、各実装開始直前に `feature/sub/SCRUM-16` から `feature/sub/SCRUM-23` の単位で切る。

**差分記録**: `backend/AGENTS.md` には「作成時 status 未指定は `TODO`」「更新は部分更新」とあるが、今回の [spec.md](./spec.md) は `status` 必須入力と、`title` / `description` / `status` 更新を定義している。本機能では憲章の「仕様を唯一の正とする」に従い、`specs/` 配下の仕様を優先する。

## プロジェクト構成

### ドキュメント (この機能)

```text
specs/001-task-basic-management/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
└── tasks.md             # 後続の /speckit-tasks で作成
```

### ソースコード (リポジトリルート)

```text
backend/
├── pom.xml
├── src/main/java/com/example/taskapp/
│   ├── TaskappApplication.java
│   ├── task/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── domain/
│   │   ├── repository/
│   │   └── dto/
│   └── common/
│       └── exception/
├── src/main/resources/
│   └── application.yaml
└── src/test/java/com/example/taskapp/
    ├── task/
    └── TaskappApplicationTests.java

frontend/
├── src/app/
├── package.json
└── AGENTS.md
```

**構成判断**: 今回の対象は backend API のみとする。`backend/AGENTS.md` のモジュラモノリス構成に合わせ、Task 関連コードは `com.example.taskapp.task` 配下へ、共通例外は `com.example.taskapp.common.exception` 配下へ配置する。Frontend は API 契約確定後に接続するため、この計画では実装対象に含めない。

## Phase 0: 調査結果

[research.md](./research.md) に記録した。未解決項目は残していない。

## Phase 1: 設計成果物

- [data-model.md](./data-model.md): Task、TaskStatus、ErrorResponse、入力DTO/出力DTOの責務を定義。
- [contracts/openapi.yaml](./contracts/openapi.yaml): REST API 5 endpoints と共通エラーレスポンスを定義。
- [quickstart.md](./quickstart.md): DB前提、テスト、起動、curl による主要検証手順を定義。
- `AGENTS.md`: ルートの共通定義であり feature 状態に依存させないため、plan reference は追記しない。

## 設計後の憲章チェック

- **仕様参照**: PASS。設計成果物は [spec.md](./spec.md) の FR-001 から FR-016、および成功基準 SC-001 から SC-007 に対応する。
- **バックエンドAPI先行**: PASS。設計対象は backend API 契約と永続化境界であり、frontend 接続は含めていない。
- **REST/user scope**: PASS。OpenAPI 契約は `userId` をすべての Task 操作パスに含め、動詞を含む URL を定義していない。
- **ドメイン不変条件**: PASS。`TaskStatus` は `TODO` / `DOING` / `DONE` のみ、削除済み Task は通常取得・更新・削除対象から除外する。時刻方針は JST API 表現と server timezone 非依存を設計成果物に反映した。
- **境界分離**: PASS。data model と contracts で Entity、DTO、ErrorResponse の責務を分けた。
- **バリデーションと観測性**: PASS。OpenAPI と quickstart に必須入力、不正 status、404、削除後取得不可の確認を含めた。
- **日本語成果物**: PASS。成果物本文は日本語で、識別子と API パスのみ原文表記を維持した。
- **Jiraブランチ運用**: PASS。親ブランチで計画を確定し、サブタスク実装開始時に `feature/sub/SCRUM-x` を切る方針を明記した。

## 複雑性の追跡

憲章チェック違反はない。

| 違反 | 必要な理由 | よりシンプルな代替案を却下した理由 |
|------|------------|------------------------------------|
| なし | 該当なし | 該当なし |
