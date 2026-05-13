package com.example.taskapp.common.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * アプリケーションの時刻設定を提供する設定クラス。
 */
@Configuration
public class TimeConfig {

	// タスク管理アプリで使用するタイムゾーンID。
	public static final String TASK_APP_ZONE_ID = "Asia/Tokyo";

	/**
	 * タスク管理アプリで使用するタイムゾーンを返す。
	 *
	 * @return タスク管理アプリで使用するタイムゾーン
	 */
	@Bean
	public ZoneId taskZoneId() {
		return ZoneId.of(TASK_APP_ZONE_ID);
	}

	/**
	 * タスク管理アプリで使用するクロックを返す。
	 *
	 * @param taskZoneId クロックに設定するタイムゾーン
	 * @return タスク管理アプリで使用するクロック
	 */
	@Bean
	public Clock clock(ZoneId taskZoneId) {
		return Clock.system(taskZoneId);
	}
}
