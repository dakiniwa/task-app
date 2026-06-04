package com.example.taskapp.task.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.task.service.TaskDeleteService;

/**
 * タスク削除APIのリクエストを扱います。
 */
@RestController
@RequestMapping("/users/{userId}/tasks")
@Validated
public class TaskDeleteController {

	// タスク削除サービス
	private final TaskDeleteService taskDeleteService;

	/**
	 * コンストラクタ。
	 *
	 * @param taskDeleteService タスク削除サービス
	 */
	public TaskDeleteController(TaskDeleteService taskDeleteService) {
		this.taskDeleteService = taskDeleteService;
	}

	/**
	 * タスクを論理削除します。
	 *
	 * @param userId ユーザーID
	 * @param taskId タスクID
	 * @return 空のレスポンス
	 */
	@DeleteMapping("/{taskId}")
	public ResponseEntity<Void> deleteTask(
			@PathVariable @NotBlank(message = "userId は必須です") String userId,
			@PathVariable @Positive(message = "taskId は1以上を指定してください") Long taskId) {
		taskDeleteService.deleteTask(userId, taskId);
		return ResponseEntity.noContent().build();
	}
}
