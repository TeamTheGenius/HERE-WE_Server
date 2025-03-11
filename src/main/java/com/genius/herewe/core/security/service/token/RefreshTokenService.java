package com.genius.herewe.core.security.service.token;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.repository.TokenRepository;

@Service
public class RefreshTokenService {
	private final TokenRepository tokenRepository;
	private final long TTL;

	public RefreshTokenService(TokenRepository tokenRepository,
		@Value("${jwt.expiration.refresh}") long TTL) {
		this.tokenRepository = tokenRepository;
		this.TTL = TTL;
	}

	public void generateToken(Long userId, String nickname, String token) {
		Token refreshToken = Token.builder()
			.userId(userId)
			.nickname(nickname)
			.token(token)
			.ttl(TTL)
			.build();

		tokenRepository.saveRefreshToken(refreshToken);
	}

	public Token findByKey(Long userId) {
		return tokenRepository.findRefreshToken(userId)
			.orElseThrow(() -> new BusinessException(REFRESH_NOT_FOUND_IN_DB));
	}

	public Token updateRefreshToken(Long userId, String newToken) {
		Optional<Token> existingToken = tokenRepository.findRefreshToken(userId);

		if (existingToken.isPresent()) {
			Token token = existingToken.get();
			Token updatedToken = Token.builder()
				.userId(token.getUserId())
				.nickname(token.getNickname())
				.token(newToken)
				.ttl(TTL)
				.build();

			return tokenRepository.saveRefreshToken(updatedToken);
		} else {
			throw new BusinessException(REFRESH_NOT_FOUND_IN_DB);
		}
	}

	public boolean isRefreshHijacked(Long userId, String token) {
		Token storedToken = findByKey(userId);
		return !storedToken.getToken().equals(token);
	}

	public void delete(Long userId) {
		tokenRepository.deleteRefreshToken(userId);
	}
}
