package com.example.taskapp.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.taskapp.common.exception.TaskNotFoundException;
import com.example.taskapp.task.domain.Task;
import com.example.taskapp.task.domain.TaskStatus;
import com.example.taskapp.task.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskDeleteService")
class TaskDeleteServiceTest {

	private static final Instant CREATED_AT = Instant.parse("2026-05-09T00:00:00Z");
	private static final Instant OLD_UPDATED_AT = Instant.parse("2026-05-09T00:10:00Z");
	private static final Instant FIXED_NOW = Instant.parse("2026-05-09T01:30:00Z");

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private Clock clock;

	@InjectMocks
	private TaskDeleteService taskDeleteService;

	@Test
	@DisplayName("指定ユーザーの未削除タスクを論理削除して更新日時を更新する")
	void deleteTaskMarksVisibleUserTaskDeletedWithFixedClock() {
		Task task = taskWithId(1L, "user-1");
		when(taskRepository.findByIdAndUserIdAndDeletedFalse(1L, "user-1"))
				.thenReturn(Optional.of(task));
		when(clock.instant()).thenReturn(FIXED_NOW);

		taskDeleteService.deleteTask("user-1", 1L);

		assertThat(task.isDeleted()).isTrue();
		assertThat(task.getCreatedAt()).isEqualTo(CREATED_AT);
		assertThat(task.getUpdatedAt()).isEqualTo(FIXED_NOW);
		verify(taskRepository).findByIdAndUserIdAndDeletedFalse(1L, "user-1");
	}

	@Test
	@DisplayName("存在しないタスクは 404 用例外を投げる")
	void deleteTaskThrowsTaskNotFoundExceptionWhenTaskIsMissing() {
		when(taskRepository.findByIdAndUserIdAndDeletedFalse(999L, "user-1"))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() -> taskDeleteService.deleteTask("user-1", 999L))
				.isInstanceOf(TaskNotFoundException.class).hasMessage("タスクが見つかりません");
		verify(taskRepository).findByIdAndUserIdAndDeletedFalse(999L, "user-1");
	}

	/**
	 * ID 設定済みのタスクを返します。
	 *
	 * @param id タスクID
	 * @param userId ユーザーID
	 * @return ID 設定済みタスク
	 */
	private Task taskWithId(Long id, String userId) {
		Task task =
				new Task(userId, "title", "description", TaskStatus.TODO, CREATED_AT, OLD_UPDATED_AT);
		ReflectionTestUtils.setField(task, "id", id);
		return task;
	}
}
