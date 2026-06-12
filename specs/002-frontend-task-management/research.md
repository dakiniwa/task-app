# 調査: フロントエンド タスク基本管理

## 決定 1: フロントエンド構成は `features/tasks` を採用する

- **決定**: タスク機能は `frontend/src/features/tasks` 配下へ集約し、`api`, `types`, `hooks`, `components` に分ける。
- **根拠**: `frontend/AGENTS.md` が示す構成に一致し、学習時に「通信」「型」「状態」「表示」の責務を一つずつ確認しやすい。
- **検討した代替案**:
  - `src/app` にすべて実装する: 初期実装は速いが、API DTO と UI 状態の分離を学びにくい。
  - `shared` に多く置く: まだ複数機能がないため共通化が早すぎる。

## 決定 2: API base URL は `NEXT_PUBLIC_API_BASE_URL` を使用する

- **決定**: backend API の接続先は `NEXT_PUBLIC_API_BASE_URL` で管理し、未設定時のローカル既定値は `http://localhost:8080` とする。
- **根拠**: `frontend/AGENTS.md` の環境変数方針に一致し、ローカル学習と将来の環境差し替えを両立できる。
- **検討した代替案**:
  - コードへ固定値を直書きする: 学習初期は単純だが、環境差し替えと保守性が落ちる。
  - サーバー側 proxy を作る: 今回は API クライアント学習が主目的であり、スコープが広がる。

## 決定 3: API クライアントは標準 `fetch` で始める

- **決定**: `features/tasks/api/taskApi.ts` に標準 `fetch` ベースの関数を定義する。
- **根拠**: 現在の依存関係に HTTP クライアントライブラリはなく、追加依存なしで API 連携、エラーハンドリング、DTO 変換を学べる。
- **検討した代替案**:
  - Axios 導入: 便利だが、現時点では追加依存の必要性が検証されていない。
  - データ取得ライブラリ導入: キャッシュや再検証の学習には有効だが、今回の CRUD 基盤には過剰。

## 決定 4: API DTO と UI 状態を分離する

- **決定**: backend 契約そのものは `taskDto.ts`、画面表示やフォーム状態は `taskUi.ts` に定義する。
- **根拠**: 憲章と `frontend/AGENTS.md` の境界分離に一致し、API 変更時の影響範囲を小さくできる。
- **検討した代替案**:
  - `TaskResponse` を画面でも直接使う: 最初は簡単だが、フォームエラー、overlay mode、選択状態など UI 固有情報と混ざる。

## 決定 5: UI 状態は React Hooks で管理する

- **決定**: `useTasks` で一覧・選択・API状態を扱い、`useTaskForm` で登録/編集フォームを扱う。グローバル状態は導入しない。
- **根拠**: `frontend/AGENTS.md` の状態管理方針に一致し、1画面中心の機能では Hooks で十分に見通せる。
- **検討した代替案**:
  - グローバルストア導入: 将来の複数画面化では有効だが、現時点では複雑性が増える。
  - すべてを `page.tsx` の `useState` に置く: 学習初期は分かりやすいが、後続フェーズで肥大化する。

## 決定 6: TaskOverlay は右ドロワー/全画面モーダルに分岐する

- **決定**: 新規登録、詳細、編集、削除確認は TaskOverlay として扱い、デスクトップでは右ドロワー、スマホでは全画面モーダルで表示する。
- **根拠**: 仕様の Clarifications に一致し、一覧を主画面に保ちながら CRUD 操作を段階的に学習できる。
- **検討した代替案**:
  - 中央モーダルのみ: 実装は単純だが、デスクトップで一覧との関係が見えにくい。
  - 別ページ遷移: ルーティング学習には有効だが、今回の最小 CRUD 学習には広がりすぎる。

## 決定 7: エラー表示は HTTP status と `details` を分けて扱う

- **決定**: `400 Bad Request` はフィールドエラー、`404 Not Found` は存在しないリソース、`500` 系は汎用エラーとして扱う。`details` がある場合は入力欄に紐付ける。
- **根拠**: `frontend/AGENTS.md` のエラーハンドリング方針と SCRUM-6 の `ErrorResponse` 契約に一致する。
- **検討した代替案**:
  - 全エラーを同じメッセージで表示する: 実装は簡単だが、フォーム改善と学習価値が下がる。

## 決定 8: 確認は `lint`、`build`、手動受け入れで開始する

- **決定**: 現時点では `npm run lint`、`npm run build`、quickstart の手動シナリオを確認基盤とする。
- **根拠**: frontend の `package.json` に自動テストスクリプトが未定義であり、テストランナー導入は別の学習判断にする方が段階的。
- **検討した代替案**:
  - Vitest/Testing Library を導入する: 将来有効だが、SCRUM-25/26 の基盤範囲を広げる。
  - 手動確認のみ: 型・lint・build の早期検出を捨てることになる。

## 決定 9: TypeScript は 6 系へ更新する

- **決定**: フロントエンドの言語バージョンは TypeScript 6 系とし、`frontend/package.json` と `frontend/package-lock.json` の `typescript` を 6 系へ揃える。
- **根拠**: TypeScript 公式の Download ページで npm 版の最新が 6.0 と案内されているため、変更依頼どおり 6 系を計画の正とする。Next.js の TypeScript 設定ドキュメントでは `next.config.ts` や `tsconfig.json` の利用が前提で、現構成と矛盾しない。
- **検討した代替案**:
  - TypeScript 5 系を維持する: 現ローカルの実解決版 `5.9.3` とは整合するが、今回の変更依頼に反する。
  - package.json だけ 6 系へ変更する: lockfile と実解決版がずれるため、再現性が落ちる。

## 決定 10: `tsconfig.json` の `target` はブラウザ対応方針と合わせて扱う

- **決定**: 現在の `frontend/tsconfig.json` の `target: "ES2017"` は、TypeScript が型チェック時に想定する JavaScript 構文レベルと、TypeScript が emit する場合の downlevel 方針を示す設定として扱う。SCRUM-25 では `target`, `lib`, `noEmit`, `moduleResolution` の役割を確認し、変更する場合は対応ブラウザ方針と `npm run lint` / `npm run build` の結果を確認してから行う。
- **根拠**: TypeScript 公式 TSConfig Reference では、`target` はどの JavaScript 機能を downlevel し、どの機能をそのまま残すかを変える設定と説明されている。また `target` は既定の `lib` にも影響するが、このプロジェクトでは `lib: ["dom", "dom.iterable", "esnext"]` を明示しているため、利用可能 API 型は `lib` 側の影響が大きい。さらに `noEmit: true` により TypeScript は JavaScript を出力せず、Next.js/SWC 側が変換を担う構成になっている。
- **検討した代替案**:
  - すぐ `ES2022` などへ上げる: モダンブラウザ前提なら候補になるが、現時点ではブラウザ対応方針が仕様化されていないため、計画上は確認事項として扱う。
  - `ES2017` を古いという理由だけで削除する: TypeScript 6 への更新とは別軸の設定であり、互換性とビルド結果を確認せず変更すると意図しない差分になる。
