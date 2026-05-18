package com.example.taskapp.common.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * エラー発生時に返す共通レスポンスを表します。
 *
 * @param code HTTP ステータスコード
 * @param message エラー概要
 * @param details 入力項目などの補足情報
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(int code, String message, List<ErrorDetail> details) {

	/**
	 * 補足情報なしの共通エラーレスポンスを作成します。
	 *
	 * @param code HTTP ステータスコード
	 * @param message エラー概要
	 */
	public ErrorResponse(int code, String message) {
		this(code, message, List.of());
	}
}
