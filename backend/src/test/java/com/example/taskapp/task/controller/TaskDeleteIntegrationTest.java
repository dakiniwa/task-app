package com.example.taskapp.task.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("TaskDelete API")
class TaskDeleteIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUpDatabase() throws Exception {
		try (Connection connection = dataSource.getConnection();
				InputStream dataset = new ClassPathResource("/dbunit/tasks.xml").getInputStream()) {
			DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
			dbUnitConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new H2DataTypeFactory());
			IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(dataset);
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		}
	}

	@Nested
	@DisplayName("204 No Content")
	class NoContent {

		@Test
		@DisplayName("指定ユーザーの未削除タスクを論理削除して一覧と詳細から除外する")
		void deleteTaskMarksVisibleUserTaskDeletedAndExcludesItFromReadResults() throws Exception {
			mockMvc.perform(delete("/users/user-1/tasks/1")).andExpect(status().isNoContent())
					.andExpect(content().string(""));

			mockMvc.perform(get("/users/user-1/tasks/1")).andExpect(status().isNotFound())
					.andExpect(notFoundErrorResponse());

			mockMvc.perform(get("/users/user-1/tasks")).andExpect(status().isOk())
					.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[*].id", not(hasItem(1))))
					.andExpect(jsonPath("$[0].id").value(2))
					.andExpect(jsonPath("$[0].updatedAt", endsWith("+09:00")));
		}
	}

	@Nested
	@DisplayName("400 Bad Request")
	class BadRequest {

		private static final String BAD_REQUEST_MESSAGE = "入力値が不正です";

		@Test
		@DisplayName("userId が空白のみの場合は共通エラーレスポンスを返す")
		void deleteTaskRejectsBlankUserId() throws Exception {
			mockMvc.perform(delete("/users/{userId}/tasks/1", " ")).andExpect(status().isBadRequest())
					.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field", endsWith("userId")))
					.andExpect(jsonPath("$.details[0].message").value("userId は必須です"));
		}

		@Test
		@DisplayName("taskId が1未満の場合は共通エラーレスポンスを返す")
		void deleteTaskRejectsNonPositiveTaskId() throws Exception {
			mockMvc.perform(delete("/users/user-1/tasks/0")).andExpect(status().isBadRequest())
					.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field", endsWith("taskId")))
					.andExpect(jsonPath("$.details[0].message").value("taskId は1以上を指定してください"));
		}

		@Test
		@DisplayName("taskId の型変換に失敗した場合は共通エラーレスポンスを返す")
		void deleteTaskRejectsInvalidTaskIdType() throws Exception {
			mockMvc.perform(delete("/users/user-1/tasks/not-a-number")).andExpect(status().isBadRequest())
					.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value("taskId"))
					.andExpect(jsonPath("$.details[0].message").value("taskId の形式が不正です"));
		}
	}

	@Nested
	@DisplayName("404 Not Found")
	class NotFound {

		@Test
		@DisplayName("存在しないタスクは共通エラーレスポンスを返す")
		void deleteTaskReturnsNotFoundForMissingTask() throws Exception {
			mockMvc.perform(delete("/users/user-1/tasks/999")).andExpect(status().isNotFound())
					.andExpect(notFoundErrorResponse());
		}

		@Test
		@DisplayName("別ユーザーのタスクは共通エラーレスポンスを返す")
		void deleteTaskReturnsNotFoundForOtherUserTask() throws Exception {
			mockMvc.perform(delete("/users/user-1/tasks/4")).andExpect(status().isNotFound())
					.andExpect(notFoundErrorResponse());
		}

		@Test
		@DisplayName("論理削除済みタスクは共通エラーレスポンスを返す")
		void deleteTaskReturnsNotFoundForDeletedTask() throws Exception {
			mockMvc.perform(delete("/users/user-1/tasks/3")).andExpect(status().isNotFound())
					.andExpect(notFoundErrorResponse());
		}
	}

	private static ResultMatcher badRequestErrorResponse(String message) {
		return result -> {
			jsonPath("$.code").value(400).match(result);
			jsonPath("$.message").value(message).match(result);
		};
	}

	private static ResultMatcher notFoundErrorResponse() {
		return result -> {
			jsonPath("$.code").value(404).match(result);
			jsonPath("$.message").value("タスクが見つかりません").match(result);
		};
	}
}
