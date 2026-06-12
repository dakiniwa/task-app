# 契約: Task API クライアント

## 目的

`frontend/src/features/tasks/api/taskApi.ts` が提供する API 呼び出しの契約を定義する。backend API の正本は `specs/001-task-basic-management/contracts/openapi.yaml` とし、この文書はフロントエンドからの利用形を固定する。

## 共通ルール

- API base URL は `NEXT_PUBLIC_API_BASE_URL` を使用する。
- 未設定時のローカル既定値は `http://localhost:8080` とする。
- `userId` は画面から指定された値を使い、認証ユーザーとして扱わない。
- `title` と `status` は送信前に必須検証する。
- `description` は任意で、未入力は `null` または空文字から API DTO へ変換する。
- エラー応答は `ErrorResponse` として読み取り、UI 表示用の `TaskApiError` へ変換する。

## 関数契約

### listTasks

```ts
listTasks(userId: string): Promise<TaskResponse[]>
```

- `GET /users/{userId}/tasks`
- 成功時は未削除タスク一覧を返す。
- `400` は userId 不正として扱う。
- `500` 系は汎用エラーとして扱う。

### createTask

```ts
createTask(userId: string, request: TaskCreateRequest): Promise<TaskResponse>
```

- `POST /users/{userId}/tasks`
- 成功時は登録済みタスクを返す。
- `400` はフォームエラーとして扱い、`details` があれば該当入力欄に紐付ける。

### getTask

```ts
getTask(userId: string, taskId: number): Promise<TaskResponse>
```

- `GET /users/{userId}/tasks/{taskId}`
- `404` は存在しない、別 userId に属する、または論理削除済みとして扱う。

### updateTask

```ts
updateTask(userId: string, taskId: number, request: TaskUpdateRequest): Promise<TaskResponse>
```

- `PUT /users/{userId}/tasks/{taskId}`
- 成功時は更新済みタスクを返す。
- `400` はフォームエラー、`404` は存在しないリソースとして扱う。

### deleteTask

```ts
deleteTask(userId: string, taskId: number): Promise<void>
```

- `DELETE /users/{userId}/tasks/{taskId}`
- 成功時は戻り値なし。
- `404` は存在しないリソースとして扱う。

## エラー変換契約

| HTTP status | UI 表示 |
|-------------|---------|
| 400 | フォームのフィールド付近に表示。`details` がない場合は画面上部にも表示 |
| 404 | 「存在しないリソース」として表示し、一覧へ戻れる状態にする |
| 500 系 | 汎用エラーメッセージを表示し、詳細はユーザーに見せない |
| その他 | API通信に失敗した旨を表示し、操作継続を許可する |

## 受け入れ条件

- API 通信処理は `features/tasks/api` に集約されている。
- 画面コンポーネントは URL 文字列を直接組み立てない。
- `TaskStatus` は `TODO`, `DOING`, `DONE` のみを型として受け付ける。
- エラー時も画面はクラッシュせず、再試行または別操作を続けられる。
