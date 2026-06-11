# Backend Test AGENTS.md

## 対象

`backend/src/test/` 配下の Java / Spring Boot テストコードに適用する。

## 基本方針

- 仕様は `specs/` 配下の機能仕様成果物を唯一の正とする
- テストは実装詳細ではなく、外部から観測できる振る舞いを中心に検証する
- テストは Arrange / Act / Assert の流れが読み取れるように書く
- AAA コメントは常用しない。テスト構造だけで Arrange / Act / Assert が読み取れる場合は書かない
- AAA コメントは、fixture 作成、mock 設定、実行、副作用検証が混在して流れが読みにくい場合のみ使用する
- 1つのテストでは1つの主要な振る舞いを検証する
- テストメソッド名は英語の camelCase とする

## テスト種別と命名

- 単体テストは product class ごとに `クラス名Test` として作成する
- Spring context、MockMvc、JPA、DBUnit など複数境界を使う検証は `XXXIntegrationTest` とする
- Repository / JPA の既知データ投入には DBUnit を使用する

## `@Nested` の使用方針

- `@Nested` は、テスト数が増える場合に分類目的で使用する
- 分類軸は実装詳細ではなく、HTTPステータス、ユースケース、public method、query method などの振る舞いに寄せる
- `@Nested` は原則1階層までとし、深い入れ子は避ける
- Spring の例外クラス名など、実装詳細だけを分類軸にしない

## テスト共通化ルール

- 共通エラーレスポンス、固定時刻、Task fixture など複数テストで繰り返すものは `backend/src/test/java/com/example/taskapp/testsupport/` 配下に置く
- 共通化は重複が複数箇所に出てから行い、早すぎる抽象化を避ける
- テスト基底クラスの継承は原則避け、static helper、matcher、factory method を優先する
- JSON レスポンスの共通検証は helper または matcher に寄せる
- テストデータ作成は、意図が読み取れる factory method を使う

## Controller テストルール

- Controller 統合テストは MockMvc を使用する
- Controller テストでは HTTP status、レスポンス body、主要な validation error を検証する
- 共通エラーフォーマットの詳細な網羅は `GlobalExceptionHandlerTest` に寄せる
- 各 Controller テストでは、その API に固有の入力条件と代表的なエラーのみを検証する

## Service / Repository テストルール

- Service 単体テストでは Repository を mock し、業務ロジック、例外、時刻更新を検証する
- Repository 統合テストでは userId scope、logical delete、並び順など DB クエリの意味を検証する
- Repository テストでは API DTO を使わず、Entity、ID、検索条件など永続化境界の型を使う

## GlobalExceptionHandlerTest ルール

- `@Nested` を使う場合は `BadRequest`、`NotFound`、`InternalServerError` など HTTPステータス単位で分類する
- request body validation、path / query parameter validation、JSON parse error など 400 系の代表ケースをここで検証する
- 500 系では内部例外メッセージをレスポンスに露出しないことを検証する
