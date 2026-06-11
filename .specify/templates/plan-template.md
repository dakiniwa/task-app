# 実装計画: [FEATURE]

**Branch**: `[###-feature-name]` | **日付**: [DATE] | **仕様**: [link]
**入力**: `/specs/[###-feature-name]/spec.md` の機能仕様

**注記**: このテンプレートは `/speckit-plan` コマンドで埋める。実行ワークフローは `.specify/templates/plan-template.md` を参照する。

## 概要

[機能仕様から主要要件と技術アプローチを抽出する]

## 技術コンテキスト

<!--
  対応必須: このセクションをプロジェクト・機能に合わせた技術詳細へ置き換える。
  以下の構造は反復を進めるためのガイドである。
-->

**言語/バージョン**: [例: Java 25, TypeScript など。不明なら 要確認]
**主要依存関係**: [例: Spring Boot, Next.js など。不明なら 要確認]
**ストレージ**: [該当する場合: PostgreSQL など。該当なしなら N/A]
**テスト**: [例: JUnit, npm test など。不明なら 要確認]
**対象プラットフォーム**: [例: Webアプリケーション、ローカル開発環境など。不明なら 要確認]
**プロジェクト種別**: [例: web-service, web-application など。不明なら 要確認]
**性能目標**: [ドメイン固有の目標。不明なら 要確認]
**制約**: [ドメイン固有の制約。不明なら 要確認]
**スケール/スコープ**: [対象ユーザー数、画面数、API数など。不明なら 要確認]

## 憲章チェック

*GATE: Phase 0 research の前に合格必須。Phase 1 design 後に再確認する。*

- **仕様参照**: 最寄りの `AGENTS.md`、`.specify/memory/constitution.md`、関連する `specs/` 配下の機能仕様成果物を確認する。矛盾があれば記録し、実装前に解消する。
- **バックエンドAPI先行**: フロントエンド統合タスクより前に、バックエンドの domain、repository、service、controller、DTO、API契約、API検証を計画する。
- **REST/user scope**: Task エンドポイントは複数形リソースパスを使い、動詞を含めず、`userId` を含める。例: `/users/{userId}/tasks/{taskId}`。
- **ドメイン不変条件**: Task status は `TODO` / `DOING` / `DONE` のみとする。削除は論理削除とし、Task の timestamps と `userId` を保持する。
- **境界分離**: バックエンド Entity/Enum と DTO を分離する。controller は service のみを呼び、service は repository のみを呼ぶ。フロントエンドのAPI DTOとUI状態も分離する。
- **バリデーションと観測性**: DTOバリデーション、Serviceの業務バリデーション、共通エラーレスポンス、4xx/5xxログ、成功・入力不正・未検出・削除フローのAPI/手動検証を含める。
- **日本語成果物**: `spec.md`, `plan.md`, `tasks.md`, `research.md`, `data-model.md`, `quickstart.md`, `contracts/` 配下の文書、チェックリスト、報告は日本語で作成する。
- **Jiraブランチ運用**: メインタスクは `feature/SCRUM-x`、サブタスクは `feature/sub/SCRUM-x` を使う。サブタスク完了時は親featureブランチへ、全サブタスク完了後は親featureブランチから `main` へPull Requestを作成する。

## プロジェクト構成

### ドキュメント (この機能)

```text
specs/[###-feature]/
├── plan.md              # このファイル (/speckit-plan の出力)
├── research.md          # Phase 0 の出力 (/speckit-plan)
├── data-model.md        # Phase 1 の出力 (/speckit-plan)
├── quickstart.md        # Phase 1 の出力 (/speckit-plan)
├── contracts/           # Phase 1 の出力 (/speckit-plan)
└── tasks.md             # Phase 2 の出力 (/speckit-tasks。/speckit-plan では作成しない)
```

### ソースコード (リポジトリルート)

<!--
  対応必須: 下のプレースホルダーツリーを、この機能の実際の構成へ置き換える。
  未使用の選択肢は削除し、採用した構成を実パスで展開する。
-->

```text
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
```

**構成判断**: [採用した構成と、その根拠となる実ディレクトリを記録する]

## 複雑性の追跡

> **憲章チェック違反があり、正当化が必要な場合のみ記入する**

| 違反 | 必要な理由 | よりシンプルな代替案を却下した理由 |
|------|------------|------------------------------------|
| [例: 追加プロジェクト] | [現在必要な理由] | [既存構成では不十分な理由] |
| [例: 追加抽象化] | [具体的な問題] | [直接実装では不十分な理由] |
