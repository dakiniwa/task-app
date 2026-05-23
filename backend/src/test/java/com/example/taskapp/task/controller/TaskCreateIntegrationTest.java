package com.example.taskapp.task.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.example.taskapp.task.repository.TaskRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("TaskCreate API")
class TaskCreateIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TaskRepository taskRepository;

	@BeforeEach
	void setUp() {
		// Arrange
		taskRepository.deleteAll();
	}

	@Nested
	@DisplayName("201 Created")
	class Created {

		@Test
		@DisplayName("有効なリクエストで作成したタスクを返す")
		void createTaskReturnsCreatedTask() throws Exception {
			// Act & Assert
			mockMvc.perform(post("/users/user-1/tasks")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"買い物メモ","description":"週末まで","status":"TODO"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.userId").value("user-1"))
				.andExpect(jsonPath("$.title").value("買い物メモ"))
				.andExpect(jsonPath("$.description").value("週末まで"))
				.andExpect(jsonPath("$.status").value("TODO"))
				.andExpect(jsonPath("$.createdAt", endsWith("+09:00")))
				.andExpect(jsonPath("$.updatedAt", endsWith("+09:00")));
		}
	}

	@Nested
	@DisplayName("400 Bad Request")
	class BadRequest {

		private static final String BAD_REQUEST_MESSAGE = "入力値が不正です";

		@Test
		@DisplayName("userId が空白のみの場合は共通エラーレスポンスを返す")
		void createTaskRejectsBlankUserId() throws Exception {
			// Act & Assert
			mockMvc.perform(post("/users/{userId}/tasks", " ")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"買い物メモ","status":"TODO"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field", endsWith("userId")))
				.andExpect(jsonPath("$.details[0].message").value("userId は必須です"));
		}

		@Test
		@DisplayName("不正な status の場合は共通エラーレスポンスを返す")
		void createTaskRejectsInvalidStatus() throws Exception {
			// Act & Assert
			mockMvc.perform(post("/users/user-1/tasks")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"買い物メモ","status":"BLOCKED"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].message").value("リクエストボディの形式が不正です"));
		}

		@Test
		@DisplayName("必須項目が不足している場合は共通エラーレスポンスを返す")
		void createTaskRejectsMissingRequiredFields() throws Exception {
			// Act & Assert
			mockMvc.perform(post("/users/user-1/tasks")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"description":"週末まで"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[*].field", containsInAnyOrder("title", "status")))
				.andExpect(jsonPath("$.details[*].message", containsInAnyOrder("title は必須です", "status は必須です")));
		}
	}

	private static ResultMatcher badRequestErrorResponse(String message) {
		return result -> {
			jsonPath("$.code").value(400).match(result);
			jsonPath("$.message").value(message).match(result);
		};
	}
}
