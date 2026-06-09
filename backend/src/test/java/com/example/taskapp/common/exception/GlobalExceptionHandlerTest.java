package com.example.taskapp.common.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@DisplayName("GlobalExceptionHandler")
@WebMvcTest({GlobalExceptionHandlerTest.TestController.class,
		GlobalExceptionHandlerTest.ValidatedTestController.class})
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Nested
	@DisplayName("400 Bad Request(Validatedクラスアノテーションとして付与しない)")
	class BadRequest {

		private static final String BAD_REQUEST_MESSAGE = "入力値が不正です";

		@Test
		@DisplayName("requestbodyのvalidationError(MethodArgumentNotValidException)を共通エラーレスポンスに変換する")
		void requestBodyValidationThrowsMethodArgumentNotValidException() throws Exception {
			// Act & Assert
			mockMvc
					.perform(post("/body-validation").contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"\"}"))
					.andExpect(status().isBadRequest()).andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value("name"))
					.andExpect(jsonPath("$.details[0].message").value("name は必須です"));
		}

		@ParameterizedTest(name = "テストケース{3}を実行")
		@CsvSource({"' ', ok, param, pathVariableのバリデーションエラー",
				"param, '', name, requestBodyのバリデーションエラー",})
		@DisplayName("path or body validationError(HandlerMethodValidationException)を共通エラーレスポンスに変換する")
		void pathOrBodyValidationThrowsHandlerMethodValidationException(String param, String name,
				String expectField, String description) throws Exception {
			// Act & Assert
			mockMvc
					.perform(post("/handler-method-validation/%s".formatted(param))
							.contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"%s\"}".formatted(name)))
					.andExpect(status().isBadRequest()).andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value(expectField))
					.andExpect(jsonPath("$.details[0].message").value("%s は必須です".formatted(expectField)));
		}

		@Test
		@DisplayName("JSONParseErrorを共通エラーレスポンスに変換する")
		void malformedJsonThrowsHttpMessageNotReadableException() throws Exception {
			// Act & Assert
			mockMvc
					.perform(post("/body-validation").contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":"))
					.andExpect(status().isBadRequest()).andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].message").value("リクエストボディの形式が不正です"));
		}

		@Test
		@DisplayName("pathVariableの型変換失敗を共通エラーレスポンスに変換する")
		void pathVariableTypeMismatchThrowsMethodArgumentTypeMismatchException() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/type-mismatch/not-a-number")).andExpect(status().isBadRequest())
					.andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value("taskId"))
					.andExpect(jsonPath("$.details[0].message").value("taskId の形式が不正です"));
		}

		@Test
		@DisplayName("必須RequestParameter不足を共通エラーレスポンスに変換する")
		void missingRequestParameterThrowsMissingServletRequestParameterException() throws Exception {
			mockMvc.perform(post("/missing-request-param")).andExpect(status().isBadRequest())
					.andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value("name"))
					.andExpect(jsonPath("$.details[0].message").value("name は必須です"));
		}
	}

	@Nested
	@DisplayName("400 Bad Request(Validatedクラスアノテーションとして付与)")
	class ValidatedBadRequest {

		private static final String BAD_REQUEST_MESSAGE = "入力値が不正です";

		@Test
		@DisplayName("requestbodyのvalidationError(MethodArgumentNotValidException)を共通エラーレスポンスに変換する")
		void requestBodyValidationThrowsMethodArgumentNotValidException() throws Exception {
			// Act & Assert
			mockMvc
					.perform(post("/validated-body-validation").contentType(MediaType.APPLICATION_JSON)
							.content("{\"name\":\"\"}"))
					.andExpect(status().isBadRequest()).andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value("name"))
					.andExpect(jsonPath("$.details[0].message").value("name は必須です"));
		}

		@ParameterizedTest(name = "テストケース{4}を実行")
		@CsvSource({"' ', ok, constraintViolation.param, param は必須です, pathVariableのバリデーションエラー",
				"param, '', name, name は必須です, requestBodyのバリデーションエラー",})
		@DisplayName("path or body validationError(HandlerMethodValidationException or ConstraintViolationException)を共通エラーレスポンスに変換する")
		void pathOrBodyValidationThrowsException(String param, String name, String expectField,
				String expectMesseage, String description) throws Exception {
			// Act & Assert
			mockMvc
					.perform(post("/constraint-validation/%s".formatted(param))
							.contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"%s\"}".formatted(name)))
					.andExpect(status().isBadRequest()).andExpect(errorResponse(400, BAD_REQUEST_MESSAGE))
					.andExpect(jsonPath("$.details[0].field").value(expectField))
					.andExpect(jsonPath("$.details[0].message").value(expectMesseage));
		}
	}

	@Nested
	@DisplayName("404 Not Found")
	class NotFound {

		@Test
		@DisplayName("TaskNotFoundException を共通エラーレスポンスに変換する")
		void taskNotFoundReturnsNotFoundErrorResponse() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/not-found")).andExpect(status().isNotFound())
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
			mockMvc.perform(get("/internal-error")).andExpect(status().isInternalServerError())
					.andExpect(errorResponse(500, "サーバー内部エラーが発生しました"))
					.andExpect(jsonPath("$.details").doesNotExist());
		}

		@Test
		@DisplayName("想定外例外の内部メッセージをレスポンスに露出しない")
		void unexpectedErrorDoesNotExposeInternalMessage() throws Exception {
			// Act & Assert
			mockMvc.perform(get("/internal-error")).andExpect(status().isInternalServerError())
					.andExpect(content().string(not(containsString("boom"))));
		}
	}

	/**
	 * レスポンスを検証する(HTTPステータスコード、エラーメッセージ)
	 * 
	 * @param code HTTPステータスコード
	 * @param message エラーメッセージ
	 * @return
	 */
	private static ResultMatcher errorResponse(int code, String message) {
		return result -> {
			jsonPath("$.code").value(code).match(result);
			jsonPath("$.message").value(message).match(result);
		};
	}

	/**
	 * @Validated を使用しないエンドポイントのテスト用コントローラー
	 */
	@RestController
	static class TestController {

		// Spring MVC が自然発生させる例外のテスト用エンドポイント
		// このエンドポイントは引数がリクエストボディのみのため、バリデーションエラー時はMethodArgumentNotValidExceptionが発生する
		@PostMapping("/body-validation")
		void bodyValidation(@Valid @RequestBody TestRequest request) {}

		// このエンドポイントは引数がリクエストボディに加えてパス変数が含まれるので、バリデーションエラー時はHandlerMethodValidationExceptionが発生する
		@PostMapping("/handler-method-validation/{param}")
		void bodyValidation(@PathVariable @NotBlank(message = "param は必須です") String param,
				@Valid @RequestBody TestRequest request) {}

		@GetMapping("/type-mismatch/{taskId}")
		void typeMismatch(@PathVariable Long taskId) {}

		@PostMapping("/missing-request-param")
		void missingRequestParam(@RequestParam String name) {}

		@GetMapping("/not-found")
		void throwTaskNotFound() {
			throw new TaskNotFoundException();
		}

		@GetMapping("/internal-error")
		void throwInternalError() {
			throw new IllegalStateException("boom");
		}
	}

	record TestRequest(@NotBlank(message = "name は必須です") String name) {
	}

	/**
	 * @Validated を使用したエンドポイントのテスト用コントローラー
	 */
	@RestController
	@Validated
	static class ValidatedTestController {

		// Spring MVC が自然発生させる例外のテスト用エンドポイント
		// このエンドポイントは引数がリクエストボディのみのため、バリデーションエラー時はMethodArgumentNotValidExceptionが発生する
		@PostMapping("/validated-body-validation")
		void bodyValidation(@Valid @RequestBody TestRequest request) {}

		// このエンドポイントは引数がリクエストボディに加えてパス変数が含まれるので、パス変数のバリデーションエラー時はConstraintViolationExceptionが発生する
		@PostMapping("/constraint-validation/{param}")
		void constraintViolation(@PathVariable @NotBlank(message = "param は必須です") String param,
				@Valid @RequestBody TestRequest request) {}
	}
}
