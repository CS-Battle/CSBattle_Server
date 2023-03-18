package com.battle.csbattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsBattleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsBattleApplication.class, args);
	}

}
