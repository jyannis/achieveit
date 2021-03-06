package com.ecnu2020.achieveit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.ecnu2020.achieveit.mapper")
public class AchieveitApplication {

	public static void main(String[] args) {
		SpringApplication.run(AchieveitApplication.class, args);
	}

}
