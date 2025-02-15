package com.genius.herewe.core.security.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.genius.herewe.core.security.service.JwtFacade;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtFacade jwtFacade;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. 허용되는 URL(Permitted URL)인 경우 검증을 거치지 않고 doFilter
		//?? 생각해보면 이미 SecurityConfig에서 permitAll을 하는데 여기서 굳이 doFilter를 할 이유가?

		// 2. header에서 access-token 추출 후, 검증(validate)

		// 3. access-token이 expired 상태라면 refresh-token 추출

		// 4. refresh-token 검증

		// 5. refresh-token이 유효하다면, access & refresh 모두 재발급

		// 6. access or refresh가 유효하지 않다면 logout 처리

		filterChain.doFilter(request, response);
	}
}
