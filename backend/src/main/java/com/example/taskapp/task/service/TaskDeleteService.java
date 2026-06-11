package com.example.taskapp.task.service;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.common.exception.TaskNotFoundException;
import com.example.taskapp.task.domain.Task;
import com.example.taskapp.task.repository.TaskRepository;

/**
 * タスク削除の業務処理を扱います。
 */
@Service
public class TaskDeleteService {

	// タスクリポジトリ
	private final TaskRepository taskRepository;

	// クロック
	private final Clock clock;

	/**
	 * コンストラクタ。
	 *
	 * @param taskRepository タスクリポジトリ
	 * @param clock クロック
	 */
	public TaskDeleteService(TaskRepository taskRepository, Clock clock) {
		this.taskRepository = taskRepository;
		this.clock = clock;
	}

	/**
	 * 指定ユーザーの未削除タスクを論理削除します。
	 *
	 * @param userId ユーザーID
	 * @param taskId タスクID
	 * @throws TaskNotFoundException タスクが存在しない場合
	 */
	@Transactional
	public void deleteTask(String userId, Long taskId) {
		Task task = taskRepository.findByIdAndUserIdAndDeletedFalse(taskId, userId)
				.orElseThrow(TaskNotFoundException::new);
		task.delete(Instant.now(clock));
	}
}
