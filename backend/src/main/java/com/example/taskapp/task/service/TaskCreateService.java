package com.example.taskapp.task.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.task.domain.Task;
import com.example.taskapp.task.dto.TaskCreateRequest;
import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.dto.TaskResponseMapper;
import com.example.taskapp.task.repository.TaskRepository;

/**
 * タスク登録の業務処理を扱います。
 */
@Service
public class TaskCreateService {

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
	public TaskCreateService(TaskRepository taskRepository, TaskResponseMapper taskResponseMapper,
			Clock clock) {
		this.taskRepository = taskRepository;
		this.taskResponseMapper = taskResponseMapper;
		this.clock = clock;
	}

	/**
	 * タスクを作成します。
	 *
	 * @param userId ユーザーID
	 * @param request タスク作成リクエスト
	 * @return 作成したタスクレスポンス
	 */
	@Transactional
	public TaskResponse createTask(String userId, TaskCreateRequest request) {
		Instant now = Instant.now(clock);
		Task task =
				new Task(userId, request.title(), request.description(), request.status(), now, now);
		Task savedTask = taskRepository.save(task);
		return taskResponseMapper.toResponse(savedTask);
	}
}
