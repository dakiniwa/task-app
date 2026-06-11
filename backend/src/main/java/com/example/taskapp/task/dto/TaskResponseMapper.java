package com.example.taskapp.task.dto;

import org.springframework.stereotype.Component;

import com.example.taskapp.common.util.JstDateTimeFormatter;
import com.example.taskapp.task.domain.Task;

/**
 * タスクエンティティをレスポンスDTOへ変換します。
 */
@Component
public class TaskResponseMapper {

	// 日付フォーマッター
	private final JstDateTimeFormatter dateTimeFormatter;

	/**
	 * コンストラクタ。
	 *
	 * @param dateTimeFormatter 日時フォーマッター
	 */
	public TaskResponseMapper(JstDateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	/**
	 * タスクエンティティをレスポンスDTOへ変換します。
	 *
	 * @param task 変換対象のタスク
	 * @return タスクレスポンス
	 */
	public TaskResponse toResponse(Task task) {
		return new TaskResponse(task.getId(), task.getUserId(), task.getTitle(), task.getDescription(),
				task.getStatus(), dateTimeFormatter.format(task.getCreatedAt()),
				dateTimeFormatter.format(task.getUpdatedAt()));
	}
}
