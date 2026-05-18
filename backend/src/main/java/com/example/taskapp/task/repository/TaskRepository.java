package com.example.taskapp.task.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.task.domain.Task;

/**
 * タスクの永続化を扱うリポジトリです。
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

	/**
	 * 指定したタスクIDとユーザーIDに一致する未削除のタスクを取得します。
	 *
	 * @param id タスクID
	 * @param userId ユーザーID
	 * @return 未削除のタスク
	 */
	Optional<Task> findByIdAndUserIdAndDeletedFalse(Long id, String userId);

	/**
	 * 指定したユーザーIDに一致する未削除のタスク一覧を作成日時の降順で取得します。
	 *
	 * @param userId ユーザーID
	 * @return 未削除のタスク一覧
	 */
	List<Task> findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc(String userId);
}
