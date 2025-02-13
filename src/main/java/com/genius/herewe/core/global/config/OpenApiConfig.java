package com.genius.herewe.core.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("HERE:WE API Documentation")
			.description("📚 HERE:WE REST API 문서입니다.")
			.version("v1.0.0");

		Server devServer = new Server()
			.url("http://localhost:8080")
			.description("Development server");

		Server prodServer = new Server()
			.url("추후 배포 예정")
			.description("Production server");

		return new OpenAPI()
			.info(info)
			.servers(List.of(devServer, prodServer));
	}
}
