package com.example.taskapp.task.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
@DisplayName("TaskRead API")
class TaskReadIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUpDatabase() throws Exception {
		// Arrange
		try (Connection connection = dataSource.getConnection();
				InputStream dataset = new ClassPathResource("/dbunit/tasks.xml").getInputStream()) {
			DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
			dbUnitConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
			IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(dataset);
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		}
	}

	@Nested
	@DisplayName("200 OK")
	class Ok {

		@Test
		@DisplayName("指定ユーザーの未削除タスク一覧だけを作成日時降順で返す")
		void listTasksReturnsOnlyVisibleUserTasks() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/user-1/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", contains(2, 1)))
				.andExpect(jsonPath("$[*].id", not(hasItems(3, 4))))
				.andExpect(jsonPath("$[*].userId", everyItem(is("user-1"))))
				.andExpect(jsonPath("$[0].title").value("newer"))
				.andExpect(jsonPath("$[0].status").value("DOING"))
				.andExpect(jsonPath("$[0].createdAt", endsWith("+09:00")))
				.andExpect(jsonPath("$[0].updatedAt", endsWith("+09:00")));
		}

		@Test
		@DisplayName("指定ユーザーの未削除タスク詳細を返す")
		void getTaskReturnsVisibleUserTask() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/user-1/tasks/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.userId").value("user-1"))
				.andExpect(jsonPath("$.title").value("older"))
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
		@DisplayName("一覧取得で userId が空白のみの場合は共通エラーレスポンスを返す")
		void listTasksRejectsBlankUserId() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/{userId}/tasks", " "))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field", endsWith("userId")))
				.andExpect(jsonPath("$.details[0].message").value("userId は必須です"));
		}

		@Test
		@DisplayName("詳細取得で userId が空白のみの場合は共通エラーレスポンスを返す")
		void getTaskRejectsBlankUserId() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/{userId}/tasks/1", " "))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field", endsWith("userId")))
				.andExpect(jsonPath("$.details[0].message").value("userId は必須です"));
		}

		@Test
		@DisplayName("詳細取得で taskId が1未満の場合は共通エラーレスポンスを返す")
		void getTaskRejectsNonPositiveTaskId() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/user-1/tasks/0"))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field", endsWith("taskId")))
				.andExpect(jsonPath("$.details[0].message").value("taskId は1以上を指定してください"));
		}
	}

	@Nested
	@DisplayName("404 Not Found")
	class NotFound {

		@Test
		@DisplayName("存在しないタスクは共通エラーレスポンスを返す")
		void getTaskReturnsNotFoundForMissingTask() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/user-1/tasks/999"))
				.andExpect(status().isNotFound())
				.andExpect(notFoundErrorResponse());
		}

		@Test
		@DisplayName("別ユーザーのタスクは共通エラーレスポンスを返す")
		void getTaskReturnsNotFoundForOtherUserTask() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/user-1/tasks/4"))
				.andExpect(status().isNotFound())
				.andExpect(notFoundErrorResponse());
		}

		@Test
		@DisplayName("論理削除済みタスクは共通エラーレスポンスを返す")
		void getTaskReturnsNotFoundForDeletedTask() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/users/user-1/tasks/3"))
				.andExpect(status().isNotFound())
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
