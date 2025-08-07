package com.genius.herewe.core.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsUtils;

import com.genius.herewe.core.security.constants.JwtRule;
import com.genius.herewe.core.security.filter.AuthenticationExceptionFilter;
import com.genius.herewe.core.security.filter.JwtAuthenticationFilter;
import com.genius.herewe.core.security.handler.OAuth2FailureHandler;
import com.genius.herewe.core.security.handler.OAuth2SuccessHandler;
import com.genius.herewe.core.security.service.CustomOAuth2Service;
import com.genius.herewe.core.security.service.JwtManager;

import lombok.RequiredArgsConstructor;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	public static final String[] PERMITTED_URI = {"/v3/api-docs/**", "/swagger-ui/**",
		"/swagger-ui.html", "/swagger-resources/**", "/api-docs/**",
		"/webjars/**", "/favicon.ico", "/api/auth/**"};
	private static final String[] PERMITTED_ROLES = {"USER", "ADMIN"};

	private final CustomCorsConfiguration customCorsConfiguration;
	private final CustomOAuth2Service customOAuth2Service;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;

	private final JwtManager jwtManager;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(CorsConfigurer
					  -> CorsConfigurer.configurationSource(customCorsConfiguration))
			.csrf(CsrfConfigurer::disable)
			.httpBasic(HttpBasicConfigurer::disable)
			.formLogin(FormLoginConfigurer::disable)
			.headers(headers -> headers
				.addHeaderWriter(new StaticHeadersWriter(JwtRule.ACCESS_REISSUED_HEADER.getValue(), "false"))
			)

			//권한 설정
			.authorizeHttpRequests(request -> request
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.requestMatchers(PERMITTED_URI).permitAll()
				.anyRequest().hasAnyRole(PERMITTED_ROLES))

			// JWT
			.sessionManagement(configurer -> configurer
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(new JwtAuthenticationFilter(jwtManager), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new AuthenticationExceptionFilter(), JwtAuthenticationFilter.class)

			// OAuth2
			.oauth2Login(customConfigure -> customConfigure
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
				.userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuth2Service)));

		return http.build();
	}
}
