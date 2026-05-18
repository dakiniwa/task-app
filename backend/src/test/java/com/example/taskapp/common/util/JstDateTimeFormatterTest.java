package com.example.taskapp.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JstDateTimeFormatter")
class JstDateTimeFormatterTest {

	private final JstDateTimeFormatter formatter = new JstDateTimeFormatter(ZoneId.of("Asia/Tokyo"));

	@Test
	@DisplayName("Instant を JST オフセット付き ISO-8601 文字列に変換する")
	void formatConvertsInstantToJstOffsetDateTime() {
		String formatted = formatter.format(Instant.parse("2026-05-09T01:30:00Z"));

		assertThat(formatted).isEqualTo("2026-05-09T10:30:00+09:00");
	}

	@Test
	@DisplayName("日付をまたぐ場合もシステムデフォルトタイムゾーンに依存せず JST で変換する")
	void formatDoesNotDependOnSystemDefaultZone() {
		String formatted = formatter.format(Instant.parse("2026-12-31T15:00:00Z"));

		assertThat(formatted).isEqualTo("2027-01-01T00:00:00+09:00");
	}
}
