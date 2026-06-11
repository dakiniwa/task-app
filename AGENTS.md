# AGENTS.md

## プロジェクト概要

タスク管理アプリをアジャイルに開発する。  
機能単位でバックエンドAPIを先に作り、その後フロントエンドから接続する。

## プロジェクト構成

```text
task-app/
  frontend/   # Next.js + TypeScript
  backend/    # Java + Spring Boot + Spring Data JPA
```

## 技術スタック

- Frontend: Next.js / TypeScript
- Backend: Java / Spring Boot / Spring Data JPA
- DB: PostgreSQL

## AGENTS.mdの分割方針

ルートのAGENTS.mdには、プロジェクト全体に関わる最小限のルールのみ記載する。
- フロントエンド固有のルールは `frontend/AGENTS.md`
- バックエンド固有のルールは `backend/AGENTS.md`

作業時は、対象ディレクトリに近いAGENTS.mdを優先して参照する。

## AI行動ルール

- 一度に1つだけ質問する
- 不明点は推測せず必ず確認する
- 勝手に仕様を拡張しない
- 実装前に仕様の認識合わせを行う
- 出力は一貫性を保つ

## 仕様参照ルール

- プロジェクト憲法（基本原則）：`.specify/memory/constitution.md`を参照する
- 仕様： `specs/` 配下の機能仕様成果物を唯一の正とする

## 優先順位

1. 一貫性
2. シンプルさ
3. 拡張性

## 共通開発ルール

- APIはREST準拠とし、URLに動詞を含めない
- レイヤ間でモデルの責務を分離する
- 変更は小さく保ち、機能単位で実装・確認する

## ブランチ運用ルール

- タスクは `feature/sub/SCRUM-x` ブランチで対応する（親ブランチ：`feature/SCRUM-x`）
- サブタスク完了時は `feature/sub/SCRUM-x` から対応する `feature/SCRUM-x` へPull Requestを作成する
- 全サブタスク完了後、`feature/SCRUM-x` から `main` へPull Requestを作成する

## 禁止事項

- `specs/` に定義されていない仕様を前提に実装しない
- statusの独自追加
- フロントエンドとバックエンドの責務混在
