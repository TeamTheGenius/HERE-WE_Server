package com.genius.herewe.core.security.facade;

import com.genius.herewe.core.security.dto.AuthRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthFacade {
	Long authorize(HttpServletResponse response, AuthRequest authRequest);

	Long reissueToken(HttpServletRequest request, HttpServletResponse response);

	void logout(HttpServletResponse response, String nickname);
}
