package com.genius.herewe.core.security.service.token;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.domain.TokenType;
import com.genius.herewe.core.security.repository.TokenRepository;

@Service
public class AuthTokenService {
	private final TokenRepository tokenRepository;
	private final long TTL;

	public AuthTokenService(TokenRepository tokenRepository,
							@Value("${jwt.expiration.registration}") long TTL) {
		this.tokenRepository = tokenRepository;
		this.TTL = TTL;
	}

	public String generateTokenForUser(Long userId, TokenType tokenType) {
		String token = UUID.randomUUID().toString();
		switch (tokenType) {
			case REGISTRATION_TOKEN -> tokenRepository.saveRegistrationToken(token, userId, TTL);
			case AUTH_TOKEN -> tokenRepository.saveAuthToken(token, userId, TTL);
		}
		return token;
	}

	public Long getUserIdFromToken(String token) {
		Optional<Token> registrationToken = tokenRepository.findRegistrationToken(token);
		if (registrationToken.isEmpty()) {
			throw new BusinessException(TOKEN_NOT_FOUND_IN_REDIS);
		}
		return registrationToken.get().getUserId();
	}

	public void validateAuthToken(Long userId, String token) {
		Token storedToken = tokenRepository.findAuthToken(userId)
			.orElseThrow(() -> new BusinessException(TOKEN_NOT_FOUND_IN_REDIS));
		if (!storedToken.getToken().equals(token)) {
			throw new BusinessException(TOKEN_NOT_FOUND_IN_REDIS);
		}
	}

	public void deleteRegistrationToken(String token) {
		tokenRepository.deleteRegistrationToken(token);
	}

	public void deleteAuthToken(Long userId) {
		tokenRepository.deleteAuthToken(userId);
	}
}

