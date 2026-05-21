package com.example.taskapp.task.dto;

import com.example.taskapp.task.domain.TaskStatus;

/**
 * タスク情報を返すレスポンスを表します。
 *
 * @param id タスクID
 * @param userId ユーザーID
 * @param title タスクタイトル
 * @param description タスク説明
 * @param status タスクステータス
 * @param createdAt 作成日時
 * @param updatedAt 更新日時
 */
public record TaskResponse(
	Long id,
	String userId,
	String title,
	String description,
	TaskStatus status,
	String createdAt,
	String updatedAt) {
}
