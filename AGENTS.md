# AGENTS.md

## プロジェクト概要

タスク管理アプリをアジャイルに開発する。  
機能単位でバックエンドAPIを先に作り、その後フロントエンドから接続する。

---

## プロジェクト構成

```text
task-app/
  frontend/   # Next.js + TypeScript
  backend/    # Java + Spring Boot + Spring Data JPA
```

---

## 技術スタック

- Frontend: Next.js / TypeScript
- Backend: Java / Spring Boot / Spring Data JPA
- DB: PostgreSQL

---

## AGENTS.mdの分割方針

ルートのAGENTS.mdには、プロジェクト全体に関わる最小限のルールのみ記載する。

- フロントエンド固有のルールは `frontend/AGENTS.md`
- バックエンド固有のルールは `backend/AGENTS.md`

作業時は、対象ディレクトリに近いAGENTS.mdを優先して参照する。

---

## AI行動ルール

- 一度に1つだけ質問する
- 不明点は推測せず必ず確認する
- 勝手に仕様を拡張しない
- 実装前に仕様の認識合わせを行う
- 出力は一貫性を保つ

---

## 仕様参照ルール

- 仕様は `.specify/` 配下の成果物を唯一の正とする
- `.specify/memory/constitution.md` を最優先で参照する
- `.specify/specs/` 配下の各specを仕様として扱う
- `.specify/` に存在しない情報は未定義として扱い、必ず確認する

---

## 優先順位

1. シンプルさ
2. 一貫性
3. 拡張性

---

## 共通開発ルール

- APIはREST準拠とし、URLに動詞を含めない
- レイヤ間でモデルの責務を分離するs
- 変更は小さく保ち、機能単位で実装・確認する

---

## 禁止事項

- `.specify/` に定義されていない仕様を前提に実装しない
- 独自のエンドポイント命名（例: `/createTask`）
- statusの独自追加
- フロントエンドとバックエンドの責務混在
