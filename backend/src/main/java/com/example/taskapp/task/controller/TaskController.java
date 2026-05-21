package com.example.taskapp.task.controller;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskapp.task.dto.TaskCreateRequest;
import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.service.TaskService;

/**
 * タスクAPIのリクエストを扱います。
 */
@RestController
@RequestMapping("/users/{userId}/tasks")
@Validated
public class TaskController {

	// タスクサービス
	private final TaskService taskService;

	/**
	 * コンストラクタ。
	 *
	 * @param taskService タスクサービス
	 */
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * タスクを作成します。
	 *
	 * @param userId ユーザーID
	 * @param request タスク作成リクエスト
	 * @return 作成したタスクレスポンス
	 */
	@PostMapping
	public ResponseEntity<TaskResponse> createTask(
			@PathVariable @NotBlank(message = "userId は必須です") String userId,
			@Valid @RequestBody TaskCreateRequest request) {
		TaskResponse response = taskService.createTask(userId, request);
		URI location = URI.create("/users/%s/tasks/%d".formatted(userId, response.id()));
		return ResponseEntity.created(location).body(response);
	}
}
