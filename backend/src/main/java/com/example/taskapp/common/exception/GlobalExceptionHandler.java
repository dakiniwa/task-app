package com.example.taskapp.common.exception;

import java.util.List;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

/**
 * アプリケーション全体の例外を共通エラーレスポンスに変換します。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 想定外例外のログ出力に使用するロガー
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * リクエストボディのバリデーションエラーを 400 レスポンスに変換します。
	 *
	 * @param exception リクエストボディのバリデーション例外
	 * @return 400 の共通エラーレスポンス
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		List<ErrorDetail> details = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(this::toErrorDetail)
			.toList();

		return badRequest(details);
	}

	/**
	 * メソッド引数のバリデーションエラーを 400 レスポンスに変換します。
	 *
	 * @param exception メソッド引数のバリデーション例外
	 * @return 400 の共通エラーレスポンス
	 */
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
		List<ErrorDetail> details = exception.getParameterValidationResults()
			.stream()
			.flatMap(result -> result.getResolvableErrors().stream()
				.map(error -> new ErrorDetail(result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
			.toList();

		return badRequest(details);
	}

	/**
	 * 制約違反のバリデーションエラーを 400 レスポンスに変換します。
	 *
	 * @param exception 制約違反のバリデーション例外
	 * @return 400 の共通エラーレスポンス
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
		List<ErrorDetail> details = exception.getConstraintViolations()
			.stream()
			.map(violation -> new ErrorDetail(violation.getPropertyPath().toString(), violation.getMessage()))
			.toList();

		return badRequest(details);
	}

	/**
	 * リクエストボディの形式不正を 400 レスポンスに変換します。
	 *
	 * @param exception リクエストボディの読み取り例外
	 * @return 400 の共通エラーレスポンス
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		List<ErrorDetail> details = List.of(new ErrorDetail(null, "リクエストボディの形式が不正です"));
		return badRequest(details);
	}

	/**
	 * タスク未検出を 404 レスポンスに変換します。
	 *
	 * @param exception タスク未検出の例外
	 * @return 404 の共通エラーレスポンス
	 */
	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException exception) {
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	/**
	 * 想定外の例外を 500 レスポンスに変換します。
	 *
	 * @param exception 想定外の例外
	 * @return 500 の共通エラーレスポンス
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception exception) {
		log.error("Unexpected error occurred", exception);
		ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "サーバー内部エラーが発生しました");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	/**
	 * 入力値不正の共通エラーレスポンスを作成します。
	 *
	 * @param details 入力項目などの補足情報
	 * @return 400 の共通エラーレスポンス
	 */
	private ResponseEntity<ErrorResponse> badRequest(List<ErrorDetail> details) {
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "入力値が不正です", details);
		return ResponseEntity.badRequest().body(response);
	}

	/**
	 * フィールドエラーを共通エラー詳細に変換します。
	 *
	 * @param fieldError フィールドエラー
	 * @return 共通エラー詳細
	 */
	private ErrorDetail toErrorDetail(FieldError fieldError) {
		return new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
	}
}
