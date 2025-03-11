package com.genius.herewe.core.security.service.token;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

	public Optional<Long> getUserIdFromToken(String token) {
		return tokenRepository.findRegistrationToken(token)
			.map(Token::getUserId);
	}

	public void deleteToken(String token) {
		tokenRepository.deleteRegistrationToken(token);
	}
}

