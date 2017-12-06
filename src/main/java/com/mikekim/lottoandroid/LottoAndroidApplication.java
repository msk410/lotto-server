package com.mikekim.lottoandroid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottoAndroidApplication {

	public static void main(String[] args) {
		SpringApplication.run(LottoAndroidApplication.class, args);
	}
}
