package com.example.taskapp.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * エラー内容の補足情報を表します。
 *
 * @param field 入力項目名
 * @param message 補足エラーメッセージ
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(String field, String message) {
}
