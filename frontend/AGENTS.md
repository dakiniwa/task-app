# Frontend AGENTS.md

## 対象

`frontend/` 配下のNext.js / TypeScriptアプリケーションに適用する。

---

## 技術スタック

- Next.js
- TypeScript

---

## 基本方針

- バックエンドAPIを呼び出すクライアントとして実装する
- 仕様は `.specify/` を唯一の正として参照する
- UIの状態とAPI DTOの型を分ける
- APIのベースURLは環境変数で管理する

---

## ディレクトリ方針

Next.jsの標準構成を優先し、必要に応じて以下の責務で分ける。

```text
src/
  app/
  features/
    tasks/
      components/
      api/
      types/
      hooks/
  shared/
    components/
    lib/

---

## 実装ルール

- API通信は `features/*/api` に集約する
- Task statusはバックエンドの定義に従う
- バックエンドのDTOとフロントエンドの表示状態を混同しない
- 削除操作はバックエンドの論理削除APIを呼び出す
- 入力値は送信前に最低限のバリデーションを行う

---

## UI方針

- 入力は最小ステップで完了できるようにする
- ユーザーが迷わないシンプルなUIを優先する
- タスクの状態変更（TODO / DOING / DONE）は直感的に操作できるようにする
- 不要な入力項目は増やさない

---

## 状態管理

- 状態管理は基本的にReact Hooksで行う
- グローバル状態は必要になるまで導入しない

---

## エラーハンドリング

- APIエラーは画面で適切にユーザーへ通知する
- 404は「存在しない」として扱う
- 500系エラーは汎用メッセージを表示する

---

## 環境変数

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```
