package com.genius.herewe.core.security.service;

import com.genius.herewe.core.user.domain.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtFacade {
	String generateAccessToken(HttpServletResponse response, User user);

	String generateRefreshToken(HttpServletResponse response, User user);

	String resolveAccessToken(HttpServletRequest request);

	String resolveRefreshToken(HttpServletRequest request);

	void validateAccessToken();

	void validateRefreshToken();

}
