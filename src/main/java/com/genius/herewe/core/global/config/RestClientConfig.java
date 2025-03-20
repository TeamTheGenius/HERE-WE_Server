package com.genius.herewe.core.global.config;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.genius.herewe.core.global.exception.BusinessException;

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
			.defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
				if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
					throw new BusinessException(INVALID_REQUEST_PARAM);
				}
				if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
					throw new BusinessException(SERVICE_IN_MAINTENANCE);
				}
				if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
					throw new BusinessException(INTERNAL_SERVER_ERROR);
				}
			})
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
