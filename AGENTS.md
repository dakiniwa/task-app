# AGENTS.md

## プロジェクト概要

タスク管理アプリをアジャイルに開発する。
機能単位で、バックエンドAPIを先に作り、その後フロントエンドから接続する。

## プロジェクト構成

```text
task-app/
  frontend/   # Next.js + TypeScript
  backend/    # Java + Spring Boot + Spring Data JPA
  docs/
  docker-compose.yml
```

## 技術スタック

- Frontend: Next.js / TypeScript
- Backend: Java / Spring Boot / Spring Data JPA
- DB: PostgreSQL

## AGENTS.mdの分割方針

ルートのAGENTS.mdには、プロジェクト全体に関わる最小限のルールだけを記載する。
- フロントエンド固有のルールは `frontend/AGENTS.md`
- バックエンド固有のルールは `backend/AGENTS.md`

作業時は、対象ディレクトリに近いAGENTS.mdを優先して参照する。

## 共通開発ルール

- 仕様は `task_app_design_detailed.md` と `docs/` を優先する
- DTOとEntityなど、境界をまたぐモデルは混同しない
- APIはREST準拠とし、URLに動詞を含めない
- Taskのstatusは `TODO` / `DOING` / `DONE` のみ使用する
- 削除は論理削除を基本とする
- 変更は小さく保ち、機能単位で実装・確認する

## 禁止事項

- 独自のエンドポイント命名（例: `/createTask`）
- statusの独自追加
- フロントエンドとバックエンドの責務混在
