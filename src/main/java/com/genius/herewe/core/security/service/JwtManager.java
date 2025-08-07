package com.genius.herewe.core.security.service;

import org.springframework.security.core.Authentication;

import com.genius.herewe.core.security.constants.JwtStatus;
import com.genius.herewe.core.user.domain.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtManager {
	void verifyIssueCondition(User user);

	String generateAccessToken(HttpServletResponse response, User user);

	String generateRefreshToken(HttpServletResponse response, User user);

	void setReissuedHeader(HttpServletResponse response);

	String resolveAccessToken(HttpServletRequest request);

	String resolveRefreshToken(HttpServletRequest request);

	JwtStatus verifyAccessToken(String accessToken);

	void verifyRefreshToken(String refreshToken);

	boolean isRefreshHijacked(Long userId, String refreshToken);

	User getPKFromRefresh(String refreshToken);

	Authentication createAuthentication(String accessToken);

	void logout(HttpServletResponse response, Long userId);
}
