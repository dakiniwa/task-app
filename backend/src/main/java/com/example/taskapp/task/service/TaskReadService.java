package com.example.taskapp.task.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskapp.common.exception.TaskNotFoundException;
import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.dto.TaskResponseMapper;
import com.example.taskapp.task.repository.TaskRepository;

/**
 * タスク読み取りの業務処理を扱います。
 */
@Service
public class TaskReadService {

	// タスクリポジトリ
	private final TaskRepository taskRepository;

	// タスクレスポンスマッパー
	private final TaskResponseMapper taskResponseMapper;

	/**
	 * コンストラクタ。
	 *
	 * @param taskRepository タスクリポジトリ
	 * @param taskResponseMapper タスクレスポンスマッパー
	 */
	public TaskReadService(TaskRepository taskRepository, TaskResponseMapper taskResponseMapper) {
		this.taskRepository = taskRepository;
		this.taskResponseMapper = taskResponseMapper;
	}

	/**
	 * 指定ユーザーの未削除タスク一覧を取得します。
	 *
	 * @param userId ユーザーID
	 * @return タスクレスポンス一覧
	 */
	@Transactional(readOnly = true)
	public List<TaskResponse> listTasks(String userId) {
		return taskRepository.findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId).stream()
				.map(taskResponseMapper::toResponse).toList();
	}

	/**
	 * 指定ユーザーの未削除タスク詳細を取得します。
	 *
	 * @param userId ユーザーID
	 * @param taskId タスクID
	 * @return タスクレスポンス
	 * @throws TaskNotFoundException タスクが存在しない場合
	 */
	@Transactional(readOnly = true)
	public TaskResponse getTask(String userId, Long taskId) {
		return taskRepository.findByIdAndUserIdAndDeletedFalse(taskId, userId)
				.map(taskResponseMapper::toResponse).orElseThrow(TaskNotFoundException::new);
	}
}
