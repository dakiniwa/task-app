package com.example.taskapp.task.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("TaskUpdate API")
class TaskUpdateIntegrationTest {

	private static final String OLD_UPDATED_AT = "2026-05-09T00:00:00+09:00";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUpDatabase() throws Exception {
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
		@DisplayName("指定ユーザーの未削除タスクを更新して取得結果にも反映する")
		void updateTaskUpdatesVisibleUserTaskAndReadResult() throws Exception {
			mockMvc.perform(put("/users/user-1/tasks/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"updated","description":"new description","status":"DOING"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.userId").value("user-1"))
				.andExpect(jsonPath("$.title").value("updated"))
				.andExpect(jsonPath("$.description").value("new description"))
				.andExpect(jsonPath("$.status").value("DOING"))
				.andExpect(jsonPath("$.createdAt", endsWith("+09:00")))
				.andExpect(jsonPath("$.updatedAt", endsWith("+09:00")))
				.andExpect(jsonPath("$.updatedAt").value(not(OLD_UPDATED_AT)));

			mockMvc.perform(get("/users/user-1/tasks/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("updated"))
				.andExpect(jsonPath("$.description").value("new description"))
				.andExpect(jsonPath("$.status").value("DOING"))
				.andExpect(jsonPath("$.updatedAt", endsWith("+09:00")))
				.andExpect(jsonPath("$.updatedAt").value(not(OLD_UPDATED_AT)));
		}
	}

	@Nested
	@DisplayName("400 Bad Request")
	class BadRequest {

		private static final String BAD_REQUEST_MESSAGE = "入力値が不正です";

		@Test
		@DisplayName("userId が空白のみの場合は共通エラーレスポンスを返す")
		void updateTaskRejectsBlankUserId() throws Exception {
			mockMvc.perform(put("/users/{userId}/tasks/1", " ")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"updated","status":"DOING"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field", endsWith("userId")))
				.andExpect(jsonPath("$.details[0].message").value("userId は必須です"));
		}

		@Test
		@DisplayName("必須項目が不足している場合は共通エラーレスポンスを返す")
		void updateTaskRejectsMissingRequiredFields() throws Exception {
			mockMvc.perform(put("/users/user-1/tasks/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"description":"new description"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[*].field", containsInAnyOrder("title", "status")))
				.andExpect(jsonPath("$.details[*].message", containsInAnyOrder("title は必須です", "status は必須です")));
		}

		@Test
		@DisplayName("不正な status の場合は共通エラーレスポンスを返す")
		void updateTaskRejectsInvalidStatus() throws Exception {
			mockMvc.perform(put("/users/user-1/tasks/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"updated","status":"BLOCKED"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].message").value("リクエストボディの形式が不正です"));
		}
	}

	@Nested
	@DisplayName("404 Not Found")
	class NotFound {

		@Test
		@DisplayName("存在しないタスクは共通エラーレスポンスを返す")
		void updateTaskReturnsNotFoundForMissingTask() throws Exception {
			mockMvc.perform(put("/users/user-1/tasks/999")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"updated","status":"DOING"}
						"""))
				.andExpect(status().isNotFound())
				.andExpect(notFoundErrorResponse());
		}

		@Test
		@DisplayName("別ユーザーのタスクは共通エラーレスポンスを返す")
		void updateTaskReturnsNotFoundForOtherUserTask() throws Exception {
			mockMvc.perform(put("/users/user-1/tasks/4")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"updated","status":"DOING"}
						"""))
				.andExpect(status().isNotFound())
				.andExpect(notFoundErrorResponse());
		}

		@Test
		@DisplayName("論理削除済みタスクは共通エラーレスポンスを返す")
		void updateTaskReturnsNotFoundForDeletedTask() throws Exception {
			mockMvc.perform(put("/users/user-1/tasks/3")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"title":"updated","status":"DOING"}
						"""))
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
