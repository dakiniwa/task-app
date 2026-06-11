package com.example.taskapp.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
import com.example.taskapp.task.dto.TaskUpdateRequest;
import com.example.taskapp.task.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskUpdateService")
class TaskUpdateServiceTest {

	private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
	private static final Instant CREATED_AT = Instant.parse("2026-05-09T00:00:00Z");
	private static final Instant OLD_UPDATED_AT = Instant.parse("2026-05-09T00:10:00Z");
	private static final Instant FIXED_NOW = Instant.parse("2026-05-09T01:30:00Z");

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private Clock clock;

	@Spy
	private TaskResponseMapper taskResponseMapper = new TaskResponseMapper(new JstDateTimeFormatter(JST));

	@InjectMocks
	private TaskUpdateService taskUpdateService;

	@Test
	@DisplayName("指定ユーザーの未削除タスクの内容と更新日時を更新する")
	void updateTaskUpdatesVisibleUserTaskWithFixedClock() {
		Task task = taskWithId(1L, "user-1", "before", "old", TaskStatus.TODO);
		when(taskRepository.findByIdAndUserIdAndDeletedFalse(1L, "user-1")).thenReturn(Optional.of(task));
		when(clock.instant()).thenReturn(FIXED_NOW);

		TaskResponse response = taskUpdateService.updateTask("user-1", 1L, validUpdateRequest());

		assertThat(task.getTitle()).isEqualTo("after");
		assertThat(task.getDescription()).isEqualTo("new description");
		assertThat(task.getStatus()).isEqualTo(TaskStatus.DOING);
		assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
		assertThat(task.getUpdatedAt()).isEqualTo(FIXED_NOW);
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.userId()).isEqualTo("user-1");
		assertThat(response.title()).isEqualTo("after");
		assertThat(response.description()).isEqualTo("new description");
		assertThat(response.status()).isEqualTo(TaskStatus.DOING);
		assertThat(response.createdAt()).isEqualTo("2026-05-09T09:00:00+09:00");
		assertThat(response.updatedAt()).isEqualTo("2026-05-09T10:30:00+09:00");
		verify(taskRepository).findByIdAndUserIdAndDeletedFalse(1L, "user-1");
	}

	@Test
	@DisplayName("存在しないタスクは 404 用例外を投げる")
	void updateTaskThrowsTaskNotFoundExceptionWhenTaskIsMissing() {
		when(taskRepository.findByIdAndUserIdAndDeletedFalse(999L, "user-1")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> taskUpdateService.updateTask("user-1", 999L, validUpdateRequest()))
			.isInstanceOf(TaskNotFoundException.class)
			.hasMessage("タスクが見つかりません");
		verify(taskRepository).findByIdAndUserIdAndDeletedFalse(999L, "user-1");
	}

	/**
	 * 有効なタスク更新リクエストを作成します。
	 *
	 * @return 有効なタスク更新リクエスト
	 */
	private TaskUpdateRequest validUpdateRequest() {
		return new TaskUpdateRequest("after", "new description", TaskStatus.DOING);
	}

	/**
	 * ID 設定済みのタスクを返します。
	 *
	 * @param id タスクID
	 * @param userId ユーザーID
	 * @param title タスクタイトル
	 * @param description タスク説明
	 * @param status タスクステータス
	 * @return ID 設定済みタスク
	 */
	private Task taskWithId(Long id, String userId, String title, String description, TaskStatus status) {
		Task task = new Task(userId, title, description, status, CREATED_AT, OLD_UPDATED_AT);
		ReflectionTestUtils.setField(task, "id", id);
		return task;
	}
}
