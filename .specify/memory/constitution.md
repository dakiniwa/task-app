<!--
Sync Impact Report
- Version change: 1.1.0 -> 1.2.0
- Modified principles:
  - I. Simplicity First -> I. シンプルさ最優先
  - II. Consistency by Design -> II. 一貫性を設計に組み込む
  - III. Backend-First Delivery -> III. バックエンドAPI先行
  - IV. Specification as Single Source of Truth -> IV. 仕様を唯一の正とする
  - V. Clear Boundaries and Extensibility -> V. 境界の明確化と拡張性
- Added sections:
  - 出力言語ポリシー
  - Jira連動ブランチ運用
- Removed sections:
  - None
- Templates requiring updates:
  - ✅ .specify/templates/plan-template.md
  - ✅ .specify/templates/spec-template.md
  - ✅ .specify/templates/tasks-template.md
  - ✅ .specify/templates/checklist-template.md
  - ✅ .specify/templates/constitution-template.md
  - ✅ .agents/skills/speckit-specify/SKILL.md
  - ✅ .agents/skills/speckit-plan/SKILL.md
  - ✅ .agents/skills/speckit-tasks/SKILL.md
  - ✅ .agents/skills/speckit-implement/SKILL.md
  - ✅ .agents/skills/speckit-git-feature/SKILL.md
  - ✅ .agents/skills/speckit-git-validate/SKILL.md
  - ✅ .specify/extensions/git/commands/speckit.git.feature.md
  - ✅ .specify/extensions/git/commands/speckit.git.validate.md
  - ✅ .specify/extensions/git/scripts/bash/git-common.sh
  - ✅ .specify/extensions/git/scripts/powershell/git-common.ps1
  - ✅ .specify/scripts/bash/common.sh
  - ✅ AGENTS.md
- Follow-up TODOs:
  - None
-->
# Task App 憲章

## Core Principles

### I. シンプルさ最優先
すべての設計・実装判断は、軽量でシンプルなユーザー体験を最優先しなければならない。
現在検証済みの必要性がない抽象化、機能、依存関係は追加してはならない。

根拠: このプロダクトの主目的は、個人のタスク整理を少ない認知負荷で支えることである。

### II. 一貫性を設計に組み込む
システムの振る舞い、命名、アーキテクチャは、バックエンド、フロントエンド、
ドキュメント全体で一貫していなければならない。新しい実装は、既存パターンに
合致することを確認してから導入しなければならない。

根拠: 一貫性は欠陥を減らし、開発速度を保ち、プロダクト成長時の保守性を高める。

### III. バックエンドAPI先行
機能実装は、バックエンドAPIの完成を先行し、その後にフロントエンド接続を行う
順序で進めなければならない。UIロジックが利用する前に、API契約を定義し、
検証しなければならない。

根拠: 安定したサービス契約は、その後のフロントエンド作業を並行しやすくし、
不安定なバックエンド前提にUIを結合することを防ぐ。

### IV. 仕様を唯一の正とする
すべての実装判断は `.specify/` 配下の成果物に追跡できなければならない。
`.specify/` に定義されていない要求は未定義として扱い、実装前に確認しなければ
ならない。仮定は仕様成果物に明示しなければならない。

根拠: 仕様駆動開発では、暗黙のスコープ拡張を避けるために、判断が明確かつ
監査可能である必要がある。

### V. 境界の明確化と拡張性
レイヤ間の責務は分離しなければならない。バックエンドではドメインモデルとDTOを
混在させてはならない。フロントエンドではAPI DTOとUI状態モデルを分離しなければ
ならない。アーキテクチャは、現在の過剰設計を避けつつ、将来的な業務利用への拡張を
妨げてはならない。

根拠: 厳格な境界は、趣味用途から業務用途へ拡張する際の適応性を保ち、回帰リスクを
下げる。

## Product Context

- 対象プロダクト: 個人利用向けタスク管理アプリケーション。
- アーキテクチャ: モジュラモノリス。
- 技術スタック:
  - Backend: Java, Spring Boot, Spring Data JPA
  - Frontend: Next.js, TypeScript
  - Database: PostgreSQL
- API方針:
  - REST準拠のリソース指向エンドポイントとする。
  - リソースパスは複数形名詞を使う。
  - URLパスに動詞を含めてはならない。
  - データはユーザー単位で管理する。
- ドメイン制約:
  - Task status は `TODO`, `DOING`, `DONE` のみに制限する。
  - 削除は原則として論理削除とする。

## Development Workflow

- 開発プロセスはアジャイルかつインクリメンタルでなければならない。
- デリバリー単位は小さく、テスト可能で、拡張可能でなければならない。
- 要求が曖昧な場合、実装前に認識合わせを行わなければならない。
- 未定義の要求は確認しなければならず、推測は禁止する。
- AI支援による変更では、確認質問は一度に1つまでとする。
- 変更は、シンプルさ、一貫性、拡張性の優先順位を守らなければならない。

## 出力言語ポリシー

- Spec Kit の成果物は日本語で作成しなければならない。
- 対象には `spec.md`, `plan.md`, `tasks.md`, `research.md`, `data-model.md`,
  `quickstart.md`, `contracts/` 配下の文書、チェックリスト、実装進捗の報告を含む。
- コード識別子、APIパス、HTTPメソッド、ステータス値、ファイルパス、コマンド、
  技術名は原文または英語表記を維持してよい。
- 既存テンプレートの見出しや説明文は、日本語成果物を生成できるように維持しなければ
  ならない。

## Jira連動ブランチ運用

- Jira のメインタスクに対応するブランチは `feature/SCRUM-x` 形式で作成しなければならない。
- Jira のサブタスクに対応するブランチは `feature/sub/SCRUM-x` 形式で作成しなければならない。
- `x` は Jira issue key の数値部分であり、例として `SCRUM-6`, `SCRUM-16` を使用する。
- サブタスク完了時は `feature/sub/SCRUM-x` から対応する親ブランチ `feature/SCRUM-x` へ
  Pull Request を作成し、レビュー後にマージしなければならない。
- すべてのサブタスクが完了した後、親ブランチ `feature/SCRUM-x` から `main` へ
  Pull Request を作成し、レビュー後にマージしなければならない。
- Spec Kit の標準採番ブランチ (`001-*`, timestamp形式) は互換目的で許可するが、
  Jiraタスクで進める作業では Jira issue key に基づくブランチ名を優先しなければならない。

## Governance

この憲章は、競合するプロセスガイダンスより優先される。すべての plan、spec、task
成果物は、実装前に憲章への整合性を確認しなければならない。

改定プロセス:
- 改定には、根拠、影響を受ける原則、依存テンプレートまたはガイダンスへの移行メモを
  含めなければならない。
- 改定内容に基づく新しい振る舞いを実装する前に、改定を承認しなければならない。
- 憲章変更は、影響を受けるテンプレートとコマンドガイダンスへ同じ更新サイクルで
  反映するか、フォローアップタスクとして明示的に追跡しなければならない。

バージョニングポリシー:
- この憲章にはセマンティックバージョニングを適用しなければならない。
- MAJOR: 互換性のないガバナンス変更、または原則の削除・再定義。
- MINOR: 新しい原則、またはガバナンス要件の実質的な追加。
- PATCH: 文言修正、明確化、非意味的な変更。

コンプライアンスレビュー:
- 各機能ワークフロー (`/speckit-specify`, `/speckit-plan`, `/speckit-tasks`) は
  憲章準拠を確認しなければならない。
- レビューでは、未定義要求の導入、責務混在、バックエンド先行/API方針違反を
  含む作業を却下しなければならない。
- Spec Kit 成果物が日本語でない場合、該当成果物を修正してから次工程へ進まなければ
  ならない。
- Jiraタスクに対応する実装レビューでは、ブランチ名とPull Requestの向きが
  Jira連動ブランチ運用に従っていることを確認しなければならない。

**Version**: 1.2.0 | **Ratified**: 2026-05-05 | **Last Amended**: 2026-05-09
