package com.example.taskapp.task.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import com.example.taskapp.task.domain.Task;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TaskRepository")
class TaskRepositoryIntegrationTest {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private TaskRepository taskRepository;

	@BeforeEach
	void setUpDatabase() throws Exception {
		// Arrange
		try (Connection connection = dataSource.getConnection();
				InputStream dataset = new ClassPathResource("/dbunit/tasks.xml").getInputStream()) {
			DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
			dbUnitConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new H2DataTypeFactory());
			IDataSet dataSet = new FlatXmlDataSetBuilder().setColumnSensing(true).build(dataset);
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		}
	}

	@Test
	@DisplayName("ユーザーの未削除タスクを作成日時の降順で取得する")
	void findAllByUserIdAndDeletedFalseOrderByCreatedAtDescReturnsUserTasksInCreatedAtDesc() {
		// Act
		List<Task> tasks = taskRepository.findAllByUserIdAndDeletedFalseOrderByCreatedAtDesc("user-1");

		// Assert
		assertThat(tasks).extracting(Task::getId).containsExactly(2L, 1L);
	}

	@Test
	@DisplayName("指定したユーザーの未削除タスクを取得する")
	void findByIdAndUserIdAndDeletedFalseReturnsVisibleTaskForUser() {
		// Act
		Optional<Task> found = taskRepository.findByIdAndUserIdAndDeletedFalse(1L, "user-1");

		// Assert
		assertThat(found).hasValueSatisfying(task -> assertThat(task.getId()).isEqualTo(1L));
	}

	@Test
	@DisplayName("他ユーザーのタスクは取得しない")
	void findByIdAndUserIdAndDeletedFalseReturnsEmptyForOtherUserTask() {
		// Act
		Optional<Task> scopedOut = taskRepository.findByIdAndUserIdAndDeletedFalse(4L, "user-1");

		// Assert
		assertThat(scopedOut).isEmpty();
	}

	@Test
	@DisplayName("論理削除済みタスクは取得しない")
	void findByIdAndUserIdAndDeletedFalseReturnsEmptyForDeletedTask() {
		// Act
		Optional<Task> deletedOut = taskRepository.findByIdAndUserIdAndDeletedFalse(3L, "user-1");

		// Assert
		assertThat(deletedOut).isEmpty();
	}
}
