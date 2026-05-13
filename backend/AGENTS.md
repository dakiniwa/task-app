# Backend AGENTS.md

## 対象

`backend/` 配下のJava / Spring Bootアプリケーションに適用する。


## 技術スタック

- Java: 25
- Spring Boot: 4
- Spring Data JPA
- PostgreSQL: 17

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

## コメント / Javadoc方針

- コメントは一般的で簡潔な内容にする
- 1文は長くなりすぎないようにし、できるだけ1行に収める
- クラスには `/** */` で概要を1文記載する
- フィールドには `//` で内容が一言でわかる説明を記載する
- メソッドには `/** */` で概要を1文、空行、`@param`、`@return`、必要に応じて `@throws` を記載する
- `@param`、`@return`、`@throws` は対象が存在する場合のみ記載する

## API挙動ルール

- 存在しない / 他ユーザーのリソース / 論理削除済みデータ → 404 Not Found
- 作成時、status未指定の場合は TODO を設定する
- 更新は部分更新とする（未指定項目は変更しない）
- 削除は論理削除とする（deleted = true）
- 削除成功時は 204 No Content を返却する

## エラーハンドリングルール

### HTTPステータスごとの対象ケース

| HTTPステータス | ケース |
|---|---|
| 404 Not Found | 存在しないタスクID / 論理削除済みタスクへのアクセス / 他ユーザーのリソース |
| 400 Bad Request | リクエストボディ・パスパラメータの入力値不正（バリデーションエラー） |
| 405 Method Not Allowed | 許可されていないHTTPメソッド |
| 500 Internal Server Error | サーバー内部エラー |

### エラーレスポンスフォーマット

- すべてのエラーレスポンスは以下のJSON構造に統一する
  - `code`: HTTPステータスコード（整数）
  - `message`: エラーの概要（必須）
  - `details`: 補足情報（任意）
- アプリケーション独自のエラーコードは持たない
- 例外は共通ハンドリングで一元処理する

## ドメインルール

Taskのstatusは以下のみ使用する。

- TODO
- DOING
- DONE
