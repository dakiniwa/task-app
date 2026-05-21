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

- controller: HTTPリクエストを受け取り、DTOを介してserviceを呼び出し、HTTPレスポンスを返す
- service: ユースケースの実行、トランザクション管理、repositoryの呼び出し、domainへの処理委譲を行う
- domain: Entity / Enum / Value Objectを配置し、業務ルールや状態変更を表現する
- repository: Entityの永続化・取得を担当し、DBアクセスを抽象化する
- dto: APIのリクエスト・レスポンス用の型を定義する

## API設計ルール

- REST APIに従う
- リソースは複数形にする
- URLに動詞を含めない
- `userId` をパスに含める

```text
GET    /users/{userId}/tasks、/users/{userId}/tasks/{taskId}
POST   /users/{userId}/tasks
PUT    /users/{userId}/tasks/{taskId}
DELETE /users/{userId}/tasks/{taskId}
```

## 実装ルール

- controllerはDTOを受け取り、serviceを呼び出し、DTOを返す（直接repositoryを呼び出さない）
- serviceはrepositoryを通じてEntityを取得・保存し、業務ルールはdomainに委譲する
- serviceからcontrollerや他モジュールのrepositoryを直接呼び出さない
- DTOとEntityは分離し、domainはDTOに依存しない
- 例外は@RestControllerAdviceで共通ハンドリングする
- 入力値の形式チェックはDTO、ユースケース判断はservice、状態変更ルールはdomainで行う
- 削除は `deleted` フラグによる論理削除とする
- 削除は `deleted` フラグによる論理削除とし、通常の取得・一覧・更新では `deleted = false` のデータのみ対象とする
- Entityは `createdAt` / `updatedAt` を持ち、DBカラムは `created_at` / `updated_at` とする

## コメント / Javadoc方針

- コメントは一般的で簡潔な内容にする（長くなりすぎないようにできるだけ1行に収める）
- クラスには `/** */` で概要を1文記載する
- フィールドには `//` で内容が一言でわかる説明を記載する
- メソッドには `/** */` で概要を1文、空行、`@param`、`@return`、必要に応じて `@throws` を記載する

## テストルール

- `backend/src/test/` 配下のテスト固有ルールは `backend/src/test/AGENTS.md` を参照する

## API挙動ルール

- 更新は部分更新とする（未指定項目は変更しない）
- 削除成功時は 204 No Content を返却する

## エラーハンドリングルール

### HTTPステータスごとの対象ケース

| HTTPステータス | ケース |
|---|---|
| 400 Bad Request | リクエストボディ・パスパラメータの入力値不正（バリデーションエラー） |
| 404 Not Found | 存在しないタスクID / 論理削除済みタスクへのアクセス / 他ユーザーのリソース |
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
- TODO（作成時、status未指定）
- DOING
- DONE
