package com.example.taskapp.common.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		// Arrange
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new TestController(validator))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setValidator(validator)
			.build();
	}

	@Nested
	@DisplayName("400 Bad Request")
	class BadRequest {

		private static final String BAD_REQUEST_MESSAGE = "入力値が不正です";

		@Test
		@DisplayName("request body の validation error を共通エラーレスポンスに変換する")
		void requestBodyValidationErrorReturnsBadRequestErrorResponse() throws Exception {
			// Act & Assert
			mockMvc.perform(post("/body-validation")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"name\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field").value("name"))
				.andExpect(jsonPath("$.details[0].message").value("name は必須です"));
		}

		@Test
		@DisplayName("constraint violation を共通エラーレスポンスに変換する")
		void constraintViolationReturnsBadRequestErrorResponse() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/parameter-validation")
					.param("name", ""))
				.andExpect(status().isBadRequest())
				.andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].field").value("name"))
				.andExpect(jsonPath("$.details[0].message").value("name は必須です"));
		}

		@Test
		@DisplayName("JSON parse error を共通エラーレスポンスに変換する")
		void malformedJsonReturnsBadRequestErrorResponse() throws Exception {
			// Act & Assert
			mockMvc.perform(post("/body-validation")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"name\":"))
				.andExpect(status().isBadRequest())
				.andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
				.andExpect(jsonPath("$.details[0].message").value("リクエストボディの形式が不正です"));
		}
	}

	@Nested
	@DisplayName("404 Not Found")
	class NotFound {

		@Test
		@DisplayName("TaskNotFoundException を共通エラーレスポンスに変換する")
		void taskNotFoundReturnsNotFoundErrorResponse() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/not-found"))
				.andExpect(status().isNotFound())
				.andExpect(errorResponse(404, "タスクが見つかりません"))
				.andExpect(jsonPath("$.details").doesNotExist());
		}
	}

	@Nested
	@DisplayName("500 Internal Server Error")
	class InternalServerError {

		@Test
		@DisplayName("想定外例外を共通エラーレスポンスに変換する")
		void unexpectedErrorReturnsInternalServerErrorResponse() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/internal-error"))
				.andExpect(status().isInternalServerError())
				.andExpect(errorResponse(500, "サーバー内部エラーが発生しました"))
				.andExpect(jsonPath("$.details").doesNotExist());
		}

		@Test
		@DisplayName("想定外例外の内部メッセージをレスポンスに露出しない")
		void unexpectedErrorDoesNotExposeInternalMessage() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/internal-error"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(not(containsString("boom"))));
		}
	}

	private static ResultMatcher errorResponse(int code, String message) {
		return result -> {
			jsonPath("$.code").value(code).match(result);
			jsonPath("$.message").value(message).match(result);
		};
	}

	@RestController
	@Validated
	static class TestController {

		private final Validator validator;

		TestController(Validator validator) {
			this.validator = validator;
		}

		@PostMapping("/body-validation")
		void bodyValidation(@Valid @RequestBody TestRequest request) {
		}

		@GetMapping("/parameter-validation")
		void parameterValidation(@RequestParam String name) {
			Set<ConstraintViolation<TestRequest>> violations = validator.validate(new TestRequest(name));
			if (!violations.isEmpty()) {
				throw new ConstraintViolationException(violations);
			}
		}

		@GetMapping("/not-found")
		void notFound() {
			throw new TaskNotFoundException();
		}

		@GetMapping("/internal-error")
		void internalError() {
			throw new IllegalStateException("boom");
		}
	}

	record TestRequest(@NotBlank(message = "name は必須です") String name) {
	}
}
