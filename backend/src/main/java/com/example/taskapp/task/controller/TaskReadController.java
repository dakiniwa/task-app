package com.example.taskapp.task.controller;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.service.TaskReadService;

/**
 * タスク読み取りAPIのリクエストを扱います。
 */
@RestController
@RequestMapping("/users/{userId}/tasks")
@Validated
public class TaskReadController {

	// タスク読み取りサービス
	private final TaskReadService taskReadService;

	/**
	 * コンストラクタ。
	 *
	 * @param taskReadService タスク読み取りサービス
	 */
	public TaskReadController(TaskReadService taskReadService) {
		this.taskReadService = taskReadService;
	}

	/**
	 * タスク一覧を取得します。
	 *
	 * @param userId ユーザーID
	 * @return タスクレスポンス一覧
	 */
	@GetMapping
	public ResponseEntity<List<TaskResponse>> listTasks(
			@PathVariable @NotBlank(message = "userId は必須です") String userId) {
		return ResponseEntity.ok(taskReadService.listTasks(userId));
	}

	/**
	 * タスク詳細を取得します。
	 *
	 * @param userId ユーザーID
	 * @param taskId タスクID
	 * @return タスクレスポンス
	 */
	@GetMapping("/{taskId}")
	public ResponseEntity<TaskResponse> getTask(
			@PathVariable @NotBlank(message = "userId は必須です") String userId,
			@PathVariable @Positive(message = "taskId は1以上を指定してください") Long taskId) {
		return ResponseEntity.ok(taskReadService.getTask(userId, taskId));
	}
}
