package com.example.taskapp.task.dto;

import com.example.taskapp.task.domain.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * タスク更新時のリクエストを表します。
 *
 * @param title タスクタイトル
 * @param description タスク説明
 * @param status タスクの進捗状態
 */
public record TaskUpdateRequest(@NotBlank(message = "title は必須です") String title, String description,
		@NotNull(message = "status は必須です") TaskStatus status) {
}
