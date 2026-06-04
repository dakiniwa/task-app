package com.example.taskapp.task.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.dto.TaskUpdateRequest;
import com.example.taskapp.task.service.TaskUpdateService;

/**
 * タスク更新APIのリクエストを扱います。
 */
@RestController
@RequestMapping("/users/{userId}/tasks")
@Validated
public class TaskUpdateController {

	// タスク更新サービス
	private final TaskUpdateService taskUpdateService;

	/**
	 * コンストラクタ。
	 *
	 * @param taskUpdateService タスク更新サービス
	 */
	public TaskUpdateController(TaskUpdateService taskUpdateService) {
		this.taskUpdateService = taskUpdateService;
	}

	/**
	 * タスクを更新します。
	 *
	 * @param userId ユーザーID
	 * @param taskId タスクID
	 * @param request タスク更新リクエスト
	 * @return 更新したタスクレスポンス
	 */
	@PutMapping("/{taskId}")
	public ResponseEntity<TaskResponse> updateTask(
			@PathVariable @NotBlank(message = "userId は必須です") String userId,
			@PathVariable @Positive(message = "taskId は1以上を指定してください") Long taskId,
			@Valid @RequestBody TaskUpdateRequest request) {
		return ResponseEntity.ok(taskUpdateService.updateTask(userId, taskId, request));
	}
}
