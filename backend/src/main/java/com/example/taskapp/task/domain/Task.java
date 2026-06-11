package com.example.taskapp.task.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 個人ユーザーが管理するタスクを表します。
 */
@Entity
@Table(name = "tasks")
public class Task {

	// タスクID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ユーザーID
	@Column(nullable = false)
	private String userId;

	// タスクタイトル
	@Column(nullable = false)
	private String title;

	// タスク説明
	@Column(columnDefinition = "text")
	private String description;

	// 進捗状態
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "varchar(16)")
	private TaskStatus status;

	// 削除フラグ
	@Column(nullable = false)
	private boolean deleted;

	// 作成日時
	@Column(nullable = false, updatable = false, columnDefinition = "timestamp")
	private Instant createdAt;

	// 更新日時
	@Column(nullable = false, columnDefinition = "timestamp")
	private Instant updatedAt;

	/**
	 * JPA のデフォルトコンストラクタ。
	 */
	protected Task() {}

	/**
	 * タスクを作成します。
	 *
	 * @param userId ユーザーID
	 * @param title タスクタイトル
	 * @param description タスク説明
	 * @param status 進捗状態
	 * @param createdAt 作成日時
	 * @param updatedAt 更新日時
	 */
	public Task(String userId, String title, String description, TaskStatus status, Instant createdAt,
			Instant updatedAt) {
		this.userId = userId;
		this.title = title;
		this.description = description;
		this.status = status;
		this.deleted = false;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * タスクIDを返します。
	 *
	 * @return タスクID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * ユーザーIDを返します。
	 *
	 * @return ユーザーID
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * タスクタイトルを返します。
	 *
	 * @return タスクタイトル
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * タスク説明を返します。
	 *
	 * @return タスク説明
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * タスクの進捗状態を返します。
	 *
	 * @return 進捗状態
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * 削除フラグを返します。
	 *
	 * @return 削除フラグ
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * 作成日時を返します。
	 *
	 * @return 作成日時
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}

	/**
	 * 更新日時を返します。
	 *
	 * @return 更新日時
	 */
	public Instant getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * タスクの更新可能な項目を更新します。
	 *
	 * @param title 更新後のタスクタイトル
	 * @param description 更新後のタスク説明
	 * @param status 更新後の進捗状態
	 * @param updatedAt 更新日時
	 */
	public void update(String title, String description, TaskStatus status, Instant updatedAt) {
		this.title = title;
		this.description = description;
		this.status = status;
		this.updatedAt = updatedAt;
	}

	/**
	 * タスクを削除します。
	 *
	 * @param updatedAt 更新日時
	 */
	public void delete(Instant updatedAt) {
		this.deleted = true;
		this.updatedAt = updatedAt;
	}
}
