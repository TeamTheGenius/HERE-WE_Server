package com.genius.herewe.core.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
	private final String BASE_URL;
	private final String KAKAO_AUTH_KEY;

	public RestClientConfig(@Value("${external.api.kakao.base-url}") String BASE_URL,
		@Value("${external.api.kakao.key}") String KAKAO_AUTH_KEY) {
		this.BASE_URL = BASE_URL;
		this.KAKAO_AUTH_KEY = KAKAO_AUTH_KEY;
	}

	@Bean
	public RestClient restClient() {
		return RestClient.builder()
			.baseUrl(BASE_URL)
			.defaultHeader("Authorization", "KakaoAK " + KAKAO_AUTH_KEY)
			.requestFactory(clientHttpRequestFactory())
			.build();
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5000);
		factory.setReadTimeout(10000);
		return factory;
	}
}
