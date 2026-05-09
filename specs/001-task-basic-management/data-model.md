# データモデル: タスク基本管理

## 概要

今回のデータモデルは backend API の永続化境界と入出力境界を分離する。JPA Entity は `domain` に置き、API DTO は `dto` に置く。通常操作では `deleted = true` の Task を返さない。

## Entity: Task

個人ユーザーが管理する作業項目。

| フィールド | 型 | 必須 | 説明 |
|------------|----|------|------|
| `id` | `Long` | はい | DB 採番のタスク識別子。API では `taskId` として扱う。 |
| `userId` | `String` | はい | 外部から与えられるユーザー識別子。認証とは結びつけない。 |
| `title` | `String` | はい | タスクのタイトル。空文字・空白のみは不可。 |
| `description` | `String` | いいえ | タスクの補足説明。未指定を許可する。 |
| `status` | `TaskStatus` | はい | `TODO`、`DOING`、`DONE` のいずれか。 |
| `deleted` | `boolean` | はい | 論理削除済みかどうか。初期値は `false`。 |
| `createdAt` | `Instant` | はい | 作成時点。backend が設定し、サーバのデフォルトタイムゾーンには依存しない。 |
| `updatedAt` | `Instant` | はい | 更新時点。backend が設定し、サーバのデフォルトタイムゾーンには依存しない。 |

### 関係

- `Task` は `userId` を文字列として保持する。
- User Entity は今回の範囲では導入しない。
- 他 Entity との関連は今回の範囲では持たない。

### 検証ルール

- `userId` は必須で、空文字・空白のみは不可。
- `title` は必須で、空文字・空白のみは不可。
- `description` は任意。
- `status` は必須で、`TODO` / `DOING` / `DONE` 以外は不可。
- 通常の一覧、詳細、更新、削除では `deleted = false` の Task のみ対象にする。
- `taskId` が存在しても `userId` が一致しない場合は、存在しない Task と同じ扱いにする。

### 時刻ルール

- 業務タイムゾーンは JST (`Asia/Tokyo`, UTC+09:00) とする。
- Entity では `createdAt` / `updatedAt` を時点として保持し、JVM や OS のデフォルトタイムゾーンに依存しない。
- 現在時刻の取得は注入した `Clock` と明示的な `ZoneId.of("Asia/Tokyo")` を使い、`ZoneId.systemDefault()` は使わない。
- API 応答では `createdAt` / `updatedAt` を JST オフセット付き ISO-8601 文字列として返す。例: `2026-05-09T10:30:00+09:00`。

### 状態遷移

`TaskStatus` に遷移制約は設けない。未削除 Task は、更新 API により `TODO`、`DOING`、`DONE` の任意の許可値へ変更できる。

```text
TODO  <-> DOING
TODO  <-> DONE
DOING <-> DONE
```

削除は status とは別の `deleted` フラグで表す。

```text
deleted=false -> deleted=true
```

`deleted=true` から `deleted=false` へ戻す復元操作は今回の範囲に含めない。

## Enum: TaskStatus

| 値 | 説明 |
|----|------|
| `TODO` | 未着手 |
| `DOING` | 作業中 |
| `DONE` | 完了 |

## API DTO: TaskCreateRequest

| フィールド | 型 | 必須 | 説明 |
|------------|----|------|------|
| `title` | `String` | はい | 登録するタスクタイトル。 |
| `description` | `String` | いいえ | 登録する説明。 |
| `status` | `TaskStatus` | はい | 登録時の進捗状態。 |

`userId` は path parameter から受け取り、request body には含めない。

## API DTO: TaskUpdateRequest

| フィールド | 型 | 必須 | 説明 |
|------------|----|------|------|
| `title` | `String` | はい | 更新後のタスクタイトル。 |
| `description` | `String` | いいえ | 更新後の説明。 |
| `status` | `TaskStatus` | はい | 更新後の進捗状態。 |

`taskId` と `userId` は path parameter から受け取り、request body には含めない。

## API DTO: TaskResponse

| フィールド | 型 | 必須 | 説明 |
|------------|----|------|------|
| `id` | `Long` | はい | タスク識別子。 |
| `userId` | `String` | はい | タスク所有者として扱う外部識別子。 |
| `title` | `String` | はい | タスクタイトル。 |
| `description` | `String` | いいえ | タスク説明。 |
| `status` | `TaskStatus` | はい | 現在の進捗状態。 |
| `createdAt` | `String` | はい | JST (`+09:00`) オフセット付き ISO-8601 形式の作成時点。 |
| `updatedAt` | `String` | はい | JST (`+09:00`) オフセット付き ISO-8601 形式の更新時点。 |

`deleted` は通常操作のレスポンスには含めない。削除済み Task は通常取得対象から除外する。

## API DTO: ErrorResponse

| フィールド | 型 | 必須 | 説明 |
|------------|----|------|------|
| `code` | `int` | はい | HTTP ステータスコード。 |
| `message` | `String` | はい | エラー概要。 |
| `details` | `array` | いいえ | 入力項目などの補足情報。 |

`code` はアプリケーション独自コードではなく HTTP ステータスコードを使う。

## API DTO: ErrorDetail

| フィールド | 型 | 必須 | 説明 |
|------------|----|------|------|
| `field` | `String` | いいえ | 入力項目名。 |
| `message` | `String` | はい | 補足エラーメッセージ。 |
