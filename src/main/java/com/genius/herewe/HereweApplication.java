package com.genius.herewe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HereweApplication {

	public static void main(String[] args) {
		SpringApplication.run(HereweApplication.class, args);
	}

}
