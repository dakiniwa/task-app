# Backend AGENTS.md

## 対象

`backend/` 配下のJava / Spring Bootアプリケーションに適用する。

## 技術スタック

- Java: 25.0.2
- Spring Boot: 4.0.6
- Spring Data JPA
- PostgreSQL

## アーキテクチャ

モジュラモノリス構成とする。

```text
com.example.taskapp
  ├─ task
  │    ├─ controller
  │    ├─ service
  │    ├─ domain
  │    ├─ repository
  │    └─ dto
  └─ common
       ├─ exception
       ├─ config
       └─ util
```

## レイヤ責務

- controller: HTTPリクエスト受け取り・レスポンス返却
- service: ビジネスロジック・トランザクション管理
- domain: Entity / Enum
- repository: DBアクセス
- dto: API入出力

## API設計ルール

- REST APIに従う
- リソースは複数形にする
- URLに動詞を含めない
- `userId` をパスに含める

```text
GET    /users/{userId}/tasks
GET    /users/{userId}/tasks/{taskId}
POST   /users/{userId}/tasks
PUT    /users/{userId}/tasks/{taskId}
DELETE /users/{userId}/tasks/{taskId}
```

## 実装ルール

- controllerはserviceのみ呼び出す
- serviceはrepositoryのみ呼び出す
- DTOとEntityは分離する
- 例外は共通ハンドリングを使用する
- バリデーションはDTOで受け、Serviceで業務ルールを確認する
- 削除は `deleted` フラグによる論理削除とする
- `created_at` / `updated_at` を持つ

## ドメインルール

Taskのstatusは以下のみ使用する。

- TODO
- DOING
- DONE
