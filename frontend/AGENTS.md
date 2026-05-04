# Frontend AGENTS.md

## 対象

`frontend/` 配下のNext.js / TypeScriptアプリケーションに適用する。

## 技術スタック

- Next.js
- TypeScript

## 基本方針

- バックエンドAPIを呼び出すクライアントとして実装する
- API仕様は `../task_app_design_detailed.md` と `../docs/` を優先する
- UIの状態とAPI DTOの型を分ける
- APIのベースURLは環境変数で管理する

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
```

## 実装ルール

- API通信処理は画面コンポーネントに直書きしない
- Task statusは `TODO` / `DOING` / `DONE` のみ扱う
- バックエンドのDTOとフロントエンドの表示状態を混同しない
- 削除操作はバックエンドの論理削除APIを呼び出す
- 入力値は送信前に最低限のバリデーションを行う

## 環境変数

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```
