package com.genius.herewe.security.config;

import com.genius.herewe.security.handler.OAuth2FailureHandler;
import com.genius.herewe.security.handler.OAuth2SuccessHandler;
import com.genius.herewe.security.service.CustomOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
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
                        .anyRequest().hasAnyRole(PERMITTED_ROLES))

                // OAuth2
                .oauth2Login(customConfigure -> customConfigure
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuth2Service)));

        return http.build();
    }
}
