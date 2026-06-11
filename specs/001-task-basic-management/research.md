# 調査結果: タスク基本管理

## 1. バックエンドAPI先行の実装範囲

- 決定: 今回は backend REST API の設計と実装を先行し、frontend 接続は後続サイクルで扱う。
- 根拠: 憲章の「バックエンドAPI先行」と [spec.md](./spec.md) の前提が一致している。API契約が固まれば frontend は DTO と UI 状態を分けて接続できる。
- 検討した代替案: frontend を同時実装する案は、API契約が変動した場合に UI 側の手戻りが増えるため採用しない。

## 2. API パスと `userId` スコープ

- 決定: Task API は `GET /users/{userId}/tasks`、`POST /users/{userId}/tasks`、`GET /users/{userId}/tasks/{taskId}`、`PUT /users/{userId}/tasks/{taskId}`、`DELETE /users/{userId}/tasks/{taskId}` とする。
- 根拠: REST 準拠、複数形リソース、URL に動詞を含めない、`userId` をパスに含めるという憲章・AGENTS 方針に合致する。別 `userId` の `taskId` は存在しないものとして扱える。
- 検討した代替案: `/tasks?userId=...` は user scope が弱く見え、所有者境界が API パスから読み取りにくいため採用しない。`/createTask` のような動詞パスは明示的に禁止されているため採用しない。

## 3. 永続化方式と依存関係

- 決定: `Task` は Spring Data JPA の Entity として PostgreSQL 18 に保存する。実装時に `spring-boot-starter-data-jpa`、`postgresql`、`spring-boot-starter-validation` を追加する。
- 根拠: プロジェクトの技術スタックが Spring Data JPA と PostgreSQL を前提としており、Task は作成時点・更新時点・削除状態を保持する必要がある。Validation は必須入力と enum 制約の入口検証に必要である。
- 検討した代替案: インメモリ保存は永続化要件を満たさない。JDBC 直書きは現在のアーキテクチャ方針より低レベルで、Repository 境界の一貫性が落ちるため採用しない。

## 4. Task ID と timestamps

- 決定: `taskId` は DB 採番の `Long` とし、`createdAt` / `updatedAt` は backend で管理する。
- 根拠: 仕様はタスクを識別できることと作成・更新時点の保持を求めている。個人利用向け CRUD では DB 採番 `Long` がシンプルで、追加の ID 生成依存を持たない。
- 検討した代替案: UUID は外部公開 ID として有用だが、今回の仕様では分散生成や推測困難性の要件がないため採用しない。

## 5. 時刻とタイムゾーン

- 決定: 業務タイムゾーンは JST (`Asia/Tokyo`, UTC+09:00) とする。永続化では時点を `Instant` として扱い、API 応答では `2026-05-09T10:30:00+09:00` のような JST オフセット付き ISO-8601 文字列へ変換する。現在時刻の取得は注入した `Clock` と明示的な `ZoneId.of("Asia/Tokyo")` を使い、`ZoneId.systemDefault()` に依存しない。
- 根拠: サーバの配置環境や OS のタイムゾーンが変わっても、利用者に見える作成時点・更新時点を JST として一貫させるため。`Instant` で時点を保持すれば、DB・JVM のデフォルトタイムゾーン差による保存値の揺れを避けやすい。
- 検討した代替案: サーバのデフォルトタイムゾーンを JST に設定する案は、環境設定漏れで挙動が変わるため採用しない。API 応答を UTC (`Z`) に統一する案は、今回の「JSTで動作」要件と利用者の期待に合わないため採用しない。

## 6. 入力バリデーション

- 決定: `userId`、`title`、`status` は必須。`description` は任意。`status` は `TODO`、`DOING`、`DONE` のみ受け付ける。
- 根拠: [spec.md](./spec.md) の FR-003、FR-015、エッジケースに明記されている。認証は導入しないため、`userId` はパスパラメータとして必ず検証する。
- 検討した代替案: status 未指定時に `TODO` を補完する案は、今回の `specs/` 配下の仕様が status 必須を定義しているため採用しない。

## 7. 更新方式

- 決定: `PUT /users/{userId}/tasks/{taskId}` は `title` と `status` を必須、`description` を任意とする更新として扱う。
- 根拠: 仕様はタイトル、説明、ステータスの更新と、`title` / `status` の必須入力検証を求めている。部分更新を表す `PATCH` は今回の API 契約に含めない。
- 検討した代替案: 部分更新は便利だが、今回の仕様に明記されておらず、未指定項目の扱いが追加仕様になるため採用しない。

## 8. 論理削除と 404 の扱い

- 決定: 削除は `deleted = true` による論理削除とする。存在しない `taskId`、別 `userId` の `taskId`、論理削除済み Task は 404 Not Found と同じ共通エラーフォーマットで返す。
- 根拠: 仕様の Clarifications とエッジケースが、別 userId の taskId を存在しないものとして扱うことを求めている。削除済み Task を通常操作から除外する要件にも合致する。
- 検討した代替案: 403 Forbidden は認証・認可を導入しない今回の範囲では意味が増えすぎるため採用しない。物理削除は FR-008 に反するため採用しない。

## 9. 共通エラーフォーマット

- 決定: エラー応答は `code`、`message`、任意の `details` を持つ JSON に統一する。`code` は HTTP ステータスコードの整数とし、アプリケーション独自コードは持たない。
- 根拠: FR-010 と FR-011 が明示している。入力不正、未検出、論理削除済み、不正 status の確認を同じ構造でテストできる。
- 検討した代替案: 独自エラーコード enum は識別しやすいが、仕様で禁止されているため採用しない。

## 10. テスト方針

- 決定: 正常系と異常系を backend の自動テストで確認する。Controller は MockMvc、Service は business rule、Repository/JPA は DBUnit で既知データを投入する統合テストで確認する。時刻のテストでは固定 `Clock` を使い、サーバのデフォルトタイムゾーンに依存しないことを確認する。
- 根拠: FR-014 が登録、一覧、詳細、更新、削除、バリデーション、共通エラーの完了条件を確認できるテストを求めている。実装開始時の確認により、Repository/JPA の既知データ検証は PostgreSQL Testcontainers ではなく DBUnit へ変更する。
- 検討した代替案: 手動 curl のみでは回帰検出が弱く、受け入れシナリオ 100% 確認の再現性が不足するため採用しない。

## 11. Service / Controller の責務分離

- 決定: `TaskService` / `TaskController` のような CRUD 集約クラスは使わず、登録・読み取り・更新・削除のユースケース単位で `TaskCreateService`、`TaskReadService`、`TaskUpdateService`、`TaskDeleteService` と対応 Controller/Test を分ける。API path、HTTP method、response schema は `contracts/openapi.yaml` の定義から変更しない。
- 根拠: 憲章の「境界の明確化と拡張性」は責務混在を避けることを求めている。登録、読み取り、更新、削除は変更理由が異なるため、同じ Service/Controller に追加し続けると単一責務を保てない。Repository、DTO、mapper、共通例外は責務が明確な共有部品として維持できる。
- 検討した代替案: REST resource 単位で `TaskController` と `TaskService` に集約する案は、初期実装は短くなるが、Phase5 以降で update/delete が追加されると変更理由が混在し、product class ごとのテストも集約されるため採用しない。

## 12. ブランチ運用

- 決定: `plan.md` と設計成果物は親ブランチ `feature/SCRUM-6` に置く。サブタスク実装は `/speckit-tasks` 後、実装開始直前に `feature/sub/SCRUM-16` から `feature/sub/SCRUM-23` の単位で切る。
- 根拠: 憲章とルート `AGENTS.md` が、メインタスクは `feature/SCRUM-x`、サブタスクは `feature/sub/SCRUM-x` と定義している。設計成果物を親に集約すると、各サブタスクが同じ契約を参照できる。
- 検討した代替案: plan 作成前からサブタスクブランチを切る案は、共通設計成果物の分散と取り込み漏れを招きやすいため採用しない。
