# Quickstart: タスク基本管理

## 前提

- 親ブランチは `feature/SCRUM-6`。
- サブタスク実装は `/speckit-tasks` 後、各実装開始直前に `feature/sub/SCRUM-16` から `feature/sub/SCRUM-23` の単位で切る。
- Backend は Java 25 / Spring Boot 4.0.6。
- DB は PostgreSQL 18。
- 認証は導入せず、`userId` は API path で渡す。
- 作成時点・更新時点の API 表現は JST (`Asia/Tokyo`, UTC+09:00) の ISO-8601 オフセット付き文字列とし、サーバのデフォルトタイムゾーンには依存させない。

## 1. Backend 依存関係を確認する

```bash
cd backend
./mvnw test
```

実装時には `pom.xml` に Spring Data JPA、PostgreSQL JDBC Driver、Spring Validation、DBUnit、H2 テスト依存関係を追加する。

## 2. PostgreSQL 接続情報を用意する

ローカル PostgreSQL に `taskapp` 用の DB とユーザーを用意し、Spring Boot から参照できるようにする。

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/taskapp
export SPRING_DATASOURCE_USERNAME=taskapp
export SPRING_DATASOURCE_PASSWORD=taskapp
```

`application.yaml` へ固定値を書く場合も、認証情報の扱いはローカル開発用途に限定する。

## 3. Backend を起動する

```bash
cd backend
./mvnw spring-boot:run
```

起動後、API は `http://localhost:8080` で確認する。

## 4. タスクを登録する

```bash
curl -i -X POST "http://localhost:8080/users/user-1/tasks" \
  -H "Content-Type: application/json" \
  -d '{"title":"買い物メモを作る","description":"週末までに必要なものを整理する","status":"TODO"}'
```

期待結果:

- HTTP `201 Created`
- レスポンスに `id`、`userId`、`title`、`description`、`status`、`createdAt`、`updatedAt` が含まれる。
- `status` は `TODO`。
- `createdAt` と `updatedAt` は `2026-05-09T10:30:00+09:00` のように JST の `+09:00` オフセットを含む。

以降の `{taskId}` は、登録レスポンスに含まれる `id` を使う。例:

```bash
export TASK_ID=1
```

## 5. タスク一覧を取得する

```bash
curl -i "http://localhost:8080/users/user-1/tasks"
```

期待結果:

- HTTP `200 OK`
- `user-1` の未削除タスクのみが配列で返る。
- 別 `userId` のタスクは含まれない。

## 6. タスク詳細を取得する

```bash
curl -i "http://localhost:8080/users/user-1/tasks/${TASK_ID}"
```

期待結果:

- HTTP `200 OK`
- 登録内容と一致する Task が返る。

## 7. タスクを更新する

```bash
curl -i -X PUT "http://localhost:8080/users/user-1/tasks/${TASK_ID}" \
  -H "Content-Type: application/json" \
  -d '{"title":"買い物メモを更新する","description":"予算も追記する","status":"DOING"}'
```

期待結果:

- HTTP `200 OK`
- `title`、`description`、`status` が更新される。
- `updatedAt` が更新され、JST の `+09:00` オフセットを含む。

## 8. サーバタイムゾーン非依存を確認する

実装テストでは固定 `Clock` を使い、JVM や OS のデフォルトタイムゾーンを参照しなくても `createdAt` / `updatedAt` が JST 表現になることを確認する。

期待結果:

- サーバのデフォルトタイムゾーンが JST 以外でも、API 応答の `createdAt` / `updatedAt` は `+09:00` オフセットを含む。
- 実装では `ZoneId.systemDefault()` に依存しない。

## 9. 別 userId の taskId を取得できないことを確認する

```bash
curl -i "http://localhost:8080/users/other-user/tasks/${TASK_ID}"
```

期待結果:

- HTTP `404 Not Found`
- 存在しない Task と同じ共通エラーフォーマットで返る。

## 10. タスクを論理削除する

```bash
curl -i -X DELETE "http://localhost:8080/users/user-1/tasks/${TASK_ID}"
```

期待結果:

- HTTP `204 No Content`
- 以後の一覧・詳細取得対象から除外される。

## 11. 削除後の取得不可を確認する

```bash
curl -i "http://localhost:8080/users/user-1/tasks/${TASK_ID}"
```

期待結果:

- HTTP `404 Not Found`
- レスポンスは `code` と `message` を含む共通エラーフォーマット。

## 12. 入力不正を確認する

### 不正 status

```bash
curl -i -X POST "http://localhost:8080/users/user-1/tasks" \
  -H "Content-Type: application/json" \
  -d '{"title":"不正ステータス","status":"BLOCKED"}'
```

期待結果:

- HTTP `400 Bad Request`
- `code` は `400`。
- `message` が含まれる。
- 必要に応じて `details` に入力項目の補足が含まれる。

### body 必須項目不足

```bash
curl -i -X POST "http://localhost:8080/users/user-1/tasks" \
  -H "Content-Type: application/json" \
  -d '{"description":"必須項目なし"}'
```

期待結果:

- HTTP `400 Bad Request`
- レスポンスは `code: 400` と `message: "入力値が不正です"` を含む。
- `details` に `title` と `status` の不足が含まれる。

### userId 空白

```bash
curl -i -X POST "http://localhost:8080/users/%20/tasks" \
  -H "Content-Type: application/json" \
  -d '{"title":"買い物メモ","status":"TODO"}'
```

期待結果:

- HTTP `400 Bad Request`
- レスポンスは `code: 400` と `message: "入力値が不正です"` を含む。
- `details` に `field: "userId"` と `message: "userId は必須です"` が含まれる。

### taskId 型変換失敗

```bash
curl -i "http://localhost:8080/users/user-1/tasks/not-a-number"
```

期待結果:

- HTTP `400 Bad Request`
- レスポンスは `code: 400` と `message: "入力値が不正です"` を含む。
- `details` に `field: "taskId"` と `message: "taskId の形式が不正です"` が含まれる。
