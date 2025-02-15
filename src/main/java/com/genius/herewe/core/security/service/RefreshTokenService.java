package com.genius.herewe.core.security.service;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.RefreshToken;
import com.genius.herewe.core.security.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final long TTL;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
		@Value("${jwt.expiration.refresh}") long TTL) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.TTL = TTL;
	}

	public void saveRefreshToken(Long userId, String nickname, String token) {
		RefreshToken refreshToken = RefreshToken.builder()
			.userId(userId)
			.nickname(nickname)
			.token(token)
			.ttl(TTL)
			.build();

		refreshTokenRepository.save(refreshToken);
	}

	public RefreshToken findByUserId(Long userId) {
		return refreshTokenRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(REFRESH_NOT_FOUND_IN_DB));
	}

	public void updateRefreshToken(Long userId, String newToken) {
		Optional<RefreshToken> existingToken = refreshTokenRepository.findById(userId);

		if (existingToken.isPresent()) {
			RefreshToken refreshToken = existingToken.get();
			RefreshToken updatedToken = RefreshToken.builder()
				.userId(refreshToken.getUserId())
				.nickname(refreshToken.getNickname())
				.token(newToken)
				.ttl(TTL)
				.build();

			refreshTokenRepository.save(updatedToken);
		} else {
			throw new BusinessException(REFRESH_NOT_FOUND_IN_DB);
		}
	}

	public boolean isRefreshHijacked(Long userId, String token) {
		RefreshToken storedToken = findByUserId(userId);
		return !storedToken.getToken().equals(token);
	}
}
