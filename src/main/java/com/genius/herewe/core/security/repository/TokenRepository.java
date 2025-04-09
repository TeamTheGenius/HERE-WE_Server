package com.genius.herewe.core.security.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.genius.herewe.core.security.domain.Token;
import com.genius.herewe.core.security.domain.TokenType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {
	public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
	public static final String REGISTRATION_TOKEN_PREFIX = "registration_token:";
	private final RedisTemplate<String, String> redisTemplate;

	public Optional<Token> findRefreshToken(Long userId) {
		return findByKey(REFRESH_TOKEN_PREFIX + userId);
	}

	public Optional<Token> findRegistrationToken(String uuidToken) {
		return findByKey(REGISTRATION_TOKEN_PREFIX + uuidToken);
	}

	private Optional<Token> findByKey(String key) {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(Token.create(entries));
	}

	public Token saveRefreshToken(Token token) {
		String key = REFRESH_TOKEN_PREFIX + token.getUserId();

		Map<String, String> map = new HashMap<>();
		map.put("userId", String.valueOf(token.getUserId()));
		map.put("nickname", token.getNickname());
		map.put("token", token.getToken());
		map.put("tokenType", TokenType.REFRESH_TOKEN.name());

		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, token.getTtl(), TimeUnit.SECONDS);

		return token;
	}

	public Token saveRegistrationToken(String tokenValue, Long userId, long ttl) {
		String key = REGISTRATION_TOKEN_PREFIX + tokenValue;

		Map<String, String> map = new HashMap<>();
		map.put("userId", String.valueOf(userId));
		map.put("token", tokenValue);
		map.put("tokenType", TokenType.REGISTRATION_TOKEN.name());

		saveToRedis(key, map, ttl);

		return Token.builder()
			.userId(userId)
			.token(tokenValue)
			.tokenType(TokenType.REGISTRATION_TOKEN)
			.ttl(ttl)
			.build();
	}

	private void saveToRedis(String key, Map<String, String> map, long ttl) {
		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
	}

	public void deleteRefreshToken(Long userId) {
		redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
	}

	public void deleteRegistrationToken(String token) {
		redisTemplate.delete(REGISTRATION_TOKEN_PREFIX + token);
	}
}
