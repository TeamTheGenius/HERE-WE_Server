package com.genius.herewe.core.security.filter;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.response.ExceptionResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (BusinessException e) {
			handleException(response, e);
			return;
		}
	}

	private void handleException(HttpServletResponse response, BusinessException e) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(e.getStatus().value());

		ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode());

		response.getWriter().write(
			objectMapper.writeValueAsString(exceptionResponse)
		);
	}
}
