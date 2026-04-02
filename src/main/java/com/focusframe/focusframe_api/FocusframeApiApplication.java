package com.focusframe.focusframe_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class FocusframeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FocusframeApiApplication.class, args);
	}

}
