package com.example.taskapp.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.taskapp.common.exception.TaskNotFoundException;
import com.example.taskapp.common.util.JstDateTimeFormatter;
import com.example.taskapp.task.domain.Task;
import com.example.taskapp.task.domain.TaskStatus;
import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.dto.TaskResponseMapper;
import com.example.taskapp.task.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskReadService")
class TaskReadServiceTest {

	private static final ZoneId JST = ZoneId.of("Asia/Tokyo");

	@Mock
	private TaskRepository taskRepository;

	@Spy
	private TaskResponseMapper taskResponseMapper = new TaskResponseMapper(new JstDateTimeFormatter(JST));

	@InjectMocks
	private TaskReadService taskReadService;

	@Test
	@DisplayName("指定ユーザーの未削除タスク一覧を作成日時降順のレスポンスとして返す")
	void listTasksReturnsVisibleUserTasksInRepositoryOrder() {
		// Arrange
		Task olderTask = taskWithId(1L, "user-1", "older", null, TaskStatus.TODO, "2026-05-09T00:00:00Z");
		Task newerTask = taskWithId(2L, "user-1", "newer", "description", TaskStatus.DOING, "2026-05-09T01:00:00Z");
		when(taskRepository.findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc("user-1"))
			.thenReturn(List.of(newerTask, olderTask));

		// Act
		List<TaskResponse> responses = taskReadService.listTasks("user-1");

		// Assert
		assertThat(responses).extracting(TaskResponse::id).containsExactly(2L, 1L);
		assertThat(responses).extracting(TaskResponse::userId).containsExactly("user-1", "user-1");
		assertThat(responses.getFirst().title()).isEqualTo("newer");
		assertThat(responses.getFirst().description()).isEqualTo("description");
		assertThat(responses.getFirst().status()).isEqualTo(TaskStatus.DOING);
		assertThat(responses.getFirst().createdAt()).isEqualTo("2026-05-09T10:00:00+09:00");
		assertThat(responses.getFirst().updatedAt()).isEqualTo("2026-05-09T10:00:00+09:00");
		verify(taskRepository).findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc("user-1");
	}

	@Test
	@DisplayName("指定ユーザーの未削除タスク詳細をレスポンスとして返す")
	void getTaskReturnsVisibleUserTask() {
		// Arrange
		Task task = taskWithId(1L, "user-1", "買い物メモ", "週末まで", TaskStatus.TODO, "2026-05-09T00:00:00Z");
		when(taskRepository.findByIdAndUserIdAndDeletedFalse(1L, "user-1")).thenReturn(Optional.of(task));

		// Act
		TaskResponse response = taskReadService.getTask("user-1", 1L);

		// Assert
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.userId()).isEqualTo("user-1");
		assertThat(response.title()).isEqualTo("買い物メモ");
		assertThat(response.description()).isEqualTo("週末まで");
		assertThat(response.status()).isEqualTo(TaskStatus.TODO);
		assertThat(response.createdAt()).isEqualTo("2026-05-09T09:00:00+09:00");
		assertThat(response.updatedAt()).isEqualTo("2026-05-09T09:00:00+09:00");
		verify(taskRepository).findByIdAndUserIdAndDeletedFalse(1L, "user-1");
	}

	@Test
	@DisplayName("存在しないタスクは 404 用例外を投げる")
	void getTaskThrowsTaskNotFoundExceptionWhenTaskIsMissing() {
		// Arrange
		when(taskRepository.findByIdAndUserIdAndDeletedFalse(999L, "user-1")).thenReturn(Optional.empty());

		// Act & Assert
		assertThatThrownBy(() -> taskReadService.getTask("user-1", 999L))
			.isInstanceOf(TaskNotFoundException.class)
			.hasMessage("タスクが見つかりません");
		verify(taskRepository).findByIdAndUserIdAndDeletedFalse(999L, "user-1");
	}

	/**
	 * ID 設定済みのタスクを返します。
	 *
	 * @param id タスクID
	 * @param userId ユーザーID
	 * @param title タスクタイトル
	 * @param description タスク説明
	 * @param status タスクステータス
	 * @param instantText 作成・更新日時
	 * @return ID 設定済みタスク
	 */
	private Task taskWithId(
			Long id,
			String userId,
			String title,
			String description,
			TaskStatus status,
			String instantText) {
		Instant instant = Instant.parse(instantText);
		Task task = new Task(userId, title, description, status, instant, instant);
		ReflectionTestUtils.setField(task, "id", id);
		return task;
	}
}
