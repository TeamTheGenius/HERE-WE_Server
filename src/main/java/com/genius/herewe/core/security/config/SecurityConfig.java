package com.genius.herewe.core.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

import com.genius.herewe.core.security.handler.OAuth2FailureHandler;
import com.genius.herewe.core.security.handler.OAuth2SuccessHandler;
import com.genius.herewe.core.security.service.CustomOAuth2Service;

import lombok.RequiredArgsConstructor;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private static final String[] PERMITTED_URI = {"/favicon.ico", "/api/auth/**"};
	private static final String[] SWAGGER_URI = {"/v3/api-docs/**",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/swagger-resources/**",
		"/api-docs/**",
		"/webjars/**"};
	private static final String[] PERMITTED_ROLES = {"USER", "ADMIN"};

	private final CustomCorsConfiguration customCorsConfiguration;
	private final CustomOAuth2Service customOAuth2Service;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(CorsConfigurer
				-> CorsConfigurer.configurationSource(customCorsConfiguration))
			.csrf(CsrfConfigurer::disable)
			.httpBasic(HttpBasicConfigurer::disable)
			.formLogin(FormLoginConfigurer::disable)

			//권한 설정
			.authorizeHttpRequests(request -> request
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.requestMatchers(SWAGGER_URI).permitAll()
				.requestMatchers(PERMITTED_URI).permitAll()
				.anyRequest().hasAnyRole(PERMITTED_ROLES))

			// OAuth2
			.oauth2Login(customConfigure -> customConfigure
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
				.userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuth2Service)));

		return http.build();
	}
}
