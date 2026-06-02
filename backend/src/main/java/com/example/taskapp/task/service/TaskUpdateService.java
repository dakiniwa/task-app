package com.example.taskapp.task.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.common.exception.TaskNotFoundException;
import com.example.taskapp.task.domain.Task;
import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.dto.TaskResponseMapper;
import com.example.taskapp.task.dto.TaskUpdateRequest;
import com.example.taskapp.task.repository.TaskRepository;

/**
 * タスク更新の業務処理を扱います。
 */
@Service
public class TaskUpdateService {

	// タスクリポジトリ
	private final TaskRepository taskRepository;

	// タスクレスポンスマッパー
	private final TaskResponseMapper taskResponseMapper;

	// クロック
	private final Clock clock;

	/**
	 * コンストラクタ。
	 *
	 * @param taskRepository タスクリポジトリ
	 * @param taskResponseMapper タスクレスポンスマッパー
	 * @param clock クロック
	 */
	public TaskUpdateService(TaskRepository taskRepository, TaskResponseMapper taskResponseMapper, Clock clock) {
		this.taskRepository = taskRepository;
		this.taskResponseMapper = taskResponseMapper;
		this.clock = clock;
	}

	/**
	 * 指定ユーザーの未削除タスクを更新します。
	 *
	 * @param userId ユーザーID
	 * @param taskId タスクID
	 * @param request タスク更新リクエスト
	 * @return 更新したタスクレスポンス
	 * @throws TaskNotFoundException タスクが存在しない場合
	 */
	@Transactional
	public TaskResponse updateTask(String userId, Long taskId, TaskUpdateRequest request) {
		Task task = taskRepository.findByIdAndUserIdAndDeletedFalse(taskId, userId)
			.orElseThrow(TaskNotFoundException::new);
		task.update(request.title(), request.description(), request.status(), Instant.now(clock));
		return taskResponseMapper.toResponse(task);
	}
}
