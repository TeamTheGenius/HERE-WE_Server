package com.genius.herewe.core.security.filter;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static com.genius.herewe.core.security.config.SecurityConfig.*;
import static com.genius.herewe.core.security.constants.JwtStatus.*;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.constants.JwtStatus;
import com.genius.herewe.core.security.service.JwtManager;
import com.genius.herewe.core.user.domain.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtManager jwtManager;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		if (isPermittedURI(request.getRequestURI())) {
			SecurityContextHolder.getContext().setAuthentication(null);
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = jwtManager.resolveAccessToken(request);
		JwtStatus accessStatus = jwtManager.verifyAccessToken(accessToken);

		if (accessStatus == VALID) {
			setAuthenticationToContext(accessToken);
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtManager.resolveRefreshToken(request);
		jwtManager.verifyRefreshToken(refreshToken);
		User user = jwtManager.getPKFromRefresh(refreshToken);

		boolean isHijacked = jwtManager.isRefreshHijacked(user.getId(), refreshToken);
		if (isHijacked) {
			jwtManager.logout(response, user.getId());
			throw new BusinessException(TOKEN_HIJACKED);
		}

		String reissuedAccessToken = jwtManager.generateAccessToken(response, user);
		jwtManager.generateRefreshToken(response, user);
		jwtManager.setReissuedHeader(response);

		setAuthenticationToContext(reissuedAccessToken);
		filterChain.doFilter(request, response);
	}

	private void setAuthenticationToContext(String accessToken) {
		Authentication authentication = jwtManager.createAuthentication(accessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private boolean isPermittedURI(String requestURI) {
		return Arrays.stream(PERMITTED_URI)
			.anyMatch(permitted -> {
				String replace = permitted.replace("*", "");
				return requestURI.contains(replace) || replace.contains(requestURI);
			});
	}
}
