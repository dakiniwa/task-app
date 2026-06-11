package com.example.taskapp.task.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("TaskCreate API")
class TaskCreateIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUpDatabase() throws Exception {
		try (Connection connection = dataSource.getConnection();
				InputStream dataset = new ClassPathResource("/dbunit/empty-tasks.xml").getInputStream()) {
			DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
			dbUnitConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new H2DataTypeFactory());
			IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(dataset);
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		}
	}

	@Nested
	@DisplayName("201 Created")
	class Created {

		@Test
		@DisplayName("有効なリクエストで作成したタスクを取得できる")
		void createTaskReturnsCreatedTask() throws Exception {
			// Act & Assert
			MvcResult created = mockMvc
					.perform(post("/users/user-1/tasks").contentType(MediaType.APPLICATION_JSON).content("""
							{"title":"買い物メモ","description":"週末まで","status":"TODO"}
							""")).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.userId").value("user-1"))
					.andExpect(jsonPath("$.title").value("買い物メモ"))
					.andExpect(jsonPath("$.description").value("週末まで"))
					.andExpect(jsonPath("$.status").value("TODO"))
					.andExpect(jsonPath("$.createdAt", endsWith("+09:00")))
					.andExpect(jsonPath("$.updatedAt", endsWith("+09:00"))).andReturn();

			String taskLocation = created.getResponse().getHeader("Location");
			assertThat(taskLocation).startsWith("/users/user-1/tasks/");

			mockMvc.perform(get(taskLocation)).andExpect(status().isOk())
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
			mockMvc
					.perform(
							post("/users/{userId}/tasks", " ").contentType(MediaType.APPLICATION_JSON).content("""
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
			mockMvc
					.perform(post("/users/user-1/tasks").contentType(MediaType.APPLICATION_JSON).content("""
							{"title":"買い物メモ","status":"BLOCKED"}
							""")).andExpect(status().isBadRequest())
					.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].message").value("リクエストボディの形式が不正です"));
		}

		@Test
		@DisplayName("必須項目が不足している場合は共通エラーレスポンスを返す")
		void createTaskRejectsMissingRequiredFields() throws Exception {
			// Act & Assert
			mockMvc
					.perform(post("/users/user-1/tasks").contentType(MediaType.APPLICATION_JSON).content("""
							{"description":"週末まで"}
							""")).andExpect(status().isBadRequest())
					.andExpect(badRequestErrorResponse(BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[*].field", containsInAnyOrder("title", "status")))
					.andExpect(
							jsonPath("$.details[*].message", containsInAnyOrder("title は必須です", "status は必須です")));
		}
	}

	private static ResultMatcher badRequestErrorResponse(String message) {
		return result -> {
			jsonPath("$.code").value(400).match(result);
			jsonPath("$.message").value(message).match(result);
		};
	}
}
