package com.example.taskapp.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.taskapp.common.util.JstDateTimeFormatter;
import com.example.taskapp.task.domain.Task;
import com.example.taskapp.task.domain.TaskStatus;
import com.example.taskapp.task.dto.TaskCreateRequest;
import com.example.taskapp.task.dto.TaskResponse;
import com.example.taskapp.task.dto.TaskResponseMapper;
import com.example.taskapp.task.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskCreateService")
class TaskCreateServiceTest {

	private static final ZoneId JST = ZoneId.of("Asia/Tokyo");
	private static final Instant FIXED_NOW = Instant.parse("2026-05-09T01:30:00Z");

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private Clock clock;

	@Spy
	private TaskResponseMapper taskResponseMapper = new TaskResponseMapper(new JstDateTimeFormatter(JST));

	@InjectMocks
	private TaskCreateService taskCreateService;

	@Test
	@DisplayName("リクエスト内容と固定時刻でタスクを保存する")
	void createTaskSavesTaskWithRequestValuesAndFixedClock() {
		// Arrange
		stubFixedClock();
		Task savedTask = savedTaskReturnedByRepository();
		when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

		// Act
		taskCreateService.createTask("user-1", validCreateRequest());

		// Assert
		ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepository).save(taskCaptor.capture());
		Task capturedTask = taskCaptor.getValue();

		assertThat(capturedTask.getUserId()).isEqualTo("user-1");
		assertThat(capturedTask.getTitle()).isEqualTo("買い物メモ");
		assertThat(capturedTask.getDescription()).isEqualTo("週末まで");
		assertThat(capturedTask.getStatus()).isEqualTo(TaskStatus.TODO);
		assertThat(capturedTask.isDeleted()).isFalse();
		assertThat(capturedTask.getCreatedAt()).isEqualTo(FIXED_NOW);
		assertThat(capturedTask.getUpdatedAt()).isEqualTo(FIXED_NOW);
	}

	@Test
	@DisplayName("作成したタスクを JST timestamp 付きレスポンスとして返す")
	void createTaskReturnsCreatedTaskResponseWithJstTimestamp() {
		// Arrange
		stubFixedClock();
		Task savedTask = savedTaskReturnedByRepository();
		when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

		// Act
		TaskResponse response = taskCreateService.createTask("user-1", validCreateRequest());

		// Assert
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.userId()).isEqualTo("user-1");
		assertThat(response.title()).isEqualTo("買い物メモ");
		assertThat(response.description()).isEqualTo("週末まで");
		assertThat(response.status()).isEqualTo(TaskStatus.TODO);
		assertThat(response.createdAt()).isEqualTo("2026-05-09T10:30:00+09:00");
		assertThat(response.updatedAt()).isEqualTo("2026-05-09T10:30:00+09:00");
	}

	/**
	 * 固定時刻を返すクロックに設定します。
	 */
	private void stubFixedClock() {
		when(clock.instant()).thenReturn(FIXED_NOW);
	}

	/**
	 * 有効なタスク作成リクエストを作成します。
	 *
	 * @return 有効なタスク作成リクエスト
	 */
	private TaskCreateRequest validCreateRequest() {
		return new TaskCreateRequest("買い物メモ", "週末まで", TaskStatus.TODO);
	}

	/**
	 * リポジトリが保存後に返すタスクを返します。
	 *
	 * @return 保存済みタスク
	 */
	private Task savedTaskReturnedByRepository() {
		Task task = new Task("user-1", "買い物メモ", "週末まで", TaskStatus.TODO, FIXED_NOW, FIXED_NOW);
		ReflectionTestUtils.setField(task, "id", 1L);
		return task;
	}
}
