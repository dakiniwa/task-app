package com.example.taskapp.common.exception;

/**
 * 操作対象のタスクが存在しない場合の例外です。
 */
public class TaskNotFoundException extends RuntimeException {

	/**
	 * タスクが存在しない場合の例外を作成します。
	 */
	public TaskNotFoundException() {
		super("タスクが見つかりません");
	}
}
