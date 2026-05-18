package com.example.taskapp.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

/**
 * 日時をアプリケーションのタイムゾーンで文字列に整形します。
 */
@Component
public class JstDateTimeFormatter {

	private final ZoneId taskZoneId;

	/**
	 * 日時フォーマッタを作成します。
	 *
	 * @param taskZoneId 整形に使用するタイムゾーン
	 */
	public JstDateTimeFormatter(ZoneId taskZoneId) {
		this.taskZoneId = taskZoneId;
	}

	/**
	 * InstantをISOオフセット日時文字列に整形します。
	 *
	 * @param instant 整形対象の日時
	 * @return ISOオフセット日時文字列
	 */
	public String format(Instant instant) {
		return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant.atZone(taskZoneId).toOffsetDateTime());
	}
}
