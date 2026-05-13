package com.example.taskapp.common.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

	public static final String TASK_APP_ZONE_ID = "Asia/Tokyo";

	@Bean
	public ZoneId taskZoneId() {
		return ZoneId.of(TASK_APP_ZONE_ID);
	}

	@Bean
	public Clock clock(ZoneId taskZoneId) {
		return Clock.system(taskZoneId);
	}
}
