package com.genius.herewe.core.security.facade;

import static com.genius.herewe.core.global.exception.ErrorCode.*;
import static com.genius.herewe.core.security.constants.JwtRule.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.dto.AuthRequest;
import com.genius.herewe.core.security.service.JwtManager;
import com.genius.herewe.core.security.service.token.AuthTokenService;
import com.genius.herewe.core.security.service.token.RefreshTokenService;
import com.genius.herewe.core.user.domain.User;
import com.genius.herewe.core.user.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthFacade implements AuthFacade {
	private final JwtManager jwtManager;
	private final UserService userService;
	private final AuthTokenService authTokenService;
	private final RefreshTokenService refreshTokenService;

	@Transactional
	public Long authorize(HttpServletResponse response, AuthRequest authRequest) {
		Long userId = authRequest.userId();
		authTokenService.validateAuthToken(userId, authRequest.token());
		User user = userService.findById(userId);

		jwtManager.generateAccessToken(response, user);
		jwtManager.generateRefreshToken(response, user);
		jwtManager.setReissuedHeader(response);

		authTokenService.deleteAuthToken(userId);
		return userId;
	}

	public Long reissueToken(HttpServletRequest request, HttpServletResponse response) {
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

		return user.getId();
	}

	@Override
	public void logout(HttpServletResponse response, String nickname) {
		User user = userService.findByNickname(nickname)
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

		Cookie cookie = new Cookie(REFRESH_PREFIX.getValue(), null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		refreshTokenService.delete(user.getId());
	}
}
