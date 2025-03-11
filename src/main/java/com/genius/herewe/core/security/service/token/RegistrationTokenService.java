package com.genius.herewe.core.security.service.token;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.repository.TokenRepository;

@Service
public class RegistrationTokenService {
	private final TokenRepository tokenRepository;
	private final long TTL;

	public RegistrationTokenService(TokenRepository tokenRepository,
		@Value("${jwt.expiration.registration}") long TTL) {
		this.tokenRepository = tokenRepository;
		this.TTL = TTL;
	}

	public String generateTokenForUser(Long userId) {
		String registrationToken = UUID.randomUUID().toString();
		tokenRepository.saveRegistrationToken(registrationToken, userId, TTL);

		return registrationToken;
	}

	public Long getUserIdFromToken(String token) {
		Optional<Token> registrationToken = tokenRepository.findRegistrationToken(token);
		if (registrationToken.isEmpty()) {
			throw new BusinessException(REGISTRATION_TOKEN_NOT_FOUND);
		}
		Long userId = registrationToken.get().getUserId();
		tokenRepository.deleteRegistrationToken(token);

		return userId;
	}

	public void deleteToken(String token) {
		tokenRepository.deleteRegistrationToken(token);
	}
}

