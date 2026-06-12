# データモデル: フロントエンド タスク基本管理

## TaskStatus

タスクの進捗状態。

| フィールド | 型 | ルール |
|------------|----|--------|
| value | `"TODO" \| "DOING" \| "DONE"` | backend 定義に従い、この3値以外は扱わない |

## API DTO

### TaskResponse

backend API から返るタスク。

| フィールド | 型 | ルール |
|------------|----|--------|
| id | `number` | taskId。詳細、更新、削除で使用する |
| userId | `string` | API パスの userId と同じスコープ |
| title | `string` | 画面に表示するタスク名 |
| description | `string \| null` | 任意の説明 |
| status | `TaskStatus` | `TODO`, `DOING`, `DONE` のみ |
| createdAt | `string` | backend の JST ISO-8601 表現をそのまま保持する |
| updatedAt | `string` | backend の JST ISO-8601 表現をそのまま保持する |

### TaskCreateRequest

登録時に backend API へ送るデータ。

| フィールド | 型 | ルール |
|------------|----|--------|
| title | `string` | 必須。空文字・空白のみは送信前にエラー |
| description | `string \| null` | 任意。未入力は `null` として扱える |
| status | `TaskStatus` | 必須 |

### TaskUpdateRequest

更新時に backend API へ送るデータ。

| フィールド | 型 | ルール |
|------------|----|--------|
| title | `string` | 必須。空文字・空白のみは送信前にエラー |
| description | `string \| null` | 任意。未入力は `null` として扱える |
| status | `TaskStatus` | 必須 |

### ErrorResponse

backend API の共通エラー。

| フィールド | 型 | ルール |
|------------|----|--------|
| code | `number` | HTTP status code |
| message | `string` | ユーザー向けメッセージの基本 |
| details | `ErrorDetail[] \| undefined` | フィールドエラーがある場合に使用 |

### ErrorDetail

| フィールド | 型 | ルール |
|------------|----|--------|
| field | `string \| undefined` | 入力欄に紐付ける場合に使用 |
| message | `string` | 表示するエラー文言 |

## UI 状態モデル

### UserContext

| フィールド | 型 | ルール |
|------------|----|--------|
| userId | `string` | 現在の操作対象。空文字・空白のみでは API 操作を完了させない |

### Task

画面表示用のタスク。初期段階では `TaskResponse` から必要な表示値を写す。

| フィールド | 型 | ルール |
|------------|----|--------|
| id | `number` | 選択、詳細、更新、削除の対象 |
| title | `string` | 一覧と詳細で表示 |
| description | `string` | `null` は空文字として UI 表示へ変換する |
| status | `TaskStatus` | status badge とフォーム選択に使用 |
| createdAt | `string` | 必要に応じて詳細表示に使う |
| updatedAt | `string` | 必要に応じて詳細表示に使う |

### TaskFormState

登録・編集フォームの状態。

| フィールド | 型 | ルール |
|------------|----|--------|
| title | `string` | 必須 |
| description | `string` | 任意 |
| status | `TaskStatus` | 必須。既定値は `TODO` |
| fieldErrors | `Record<string, string>` | 送信前検証または `ErrorResponse.details` から設定 |

### TaskOverlayState

一覧上に重ねる操作領域。

| フィールド | 型 | ルール |
|------------|----|--------|
| mode | `"closed" \| "create" \| "detail" \| "edit" \| "deleteConfirm"` | 表示状態を表す |
| taskId | `number \| null` | 詳細、編集、削除確認で対象を持つ |
| presentation | `"desktop-drawer" \| "mobile-fullscreen"` | 画面幅に応じた表示形態 |

### OperationFeedback

| フィールド | 型 | ルール |
|------------|----|--------|
| loading | `boolean` | API 処理中の表示制御 |
| successMessage | `string \| null` | 登録、更新、削除の成功通知 |
| errorMessage | `string \| null` | ユーザー向けエラー通知 |

## 状態遷移

### TaskOverlay

```text
closed
  ├─ 新規登録を開く -> create
  ├─ タスクを選択 -> detail
detail
  ├─ 編集を選択 -> edit
  ├─ 削除を選択 -> deleteConfirm
  └─ 閉じる -> closed
edit
  ├─ 保存成功 -> detail
  └─ 閉じる -> closed
create
  ├─ 登録成功 -> closed
  └─ 閉じる -> closed
deleteConfirm
  ├─ 削除成功 -> closed
  └─ キャンセル -> detail
```

### Task API 操作

- 一覧取得成功: `TaskResponse[]` を `Task[]` へ変換し、現在の一覧を置き換える。
- 登録成功: 成功メッセージを表示し、一覧を再取得または追加反映する。
- 更新成功: 成功メッセージを表示し、一覧と詳細の表示を更新する。
- 削除成功: 成功メッセージを表示し、対象タスクを一覧と詳細から取り除く。
- API 失敗: `ErrorResponse.message` を基本に表示し、`details` があればフォームへ反映する。
