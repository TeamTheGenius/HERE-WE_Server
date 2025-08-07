package com.genius.herewe.core.security.repository;

import static com.genius.herewe.core.security.domain.TokenType.*;

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
	private final RedisTemplate<String, String> redisTemplate;

	public Optional<Token> findRefreshToken(Long userId) {
		return findByKey(REFRESH_TOKEN.getPREFIX() + userId);
	}

	public Optional<Token> findRegistrationToken(String uuidToken) {
		return findByKey(REGISTRATION_TOKEN.getPREFIX() + uuidToken);
	}

	public Optional<Token> findAuthToken(Long userId) {
		return findByKey(AUTH_TOKEN.getPREFIX() + userId);
	}

	private Optional<Token> findByKey(String key) {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(Token.create(entries));
	}

	public Token saveRefreshToken(Token token) {
		String key = REFRESH_TOKEN.getPREFIX() + token.getUserId();

		Map<String, String> map = new HashMap<>();
		map.put("userId", String.valueOf(token.getUserId()));
		map.put("nickname", token.getNickname());
		map.put("token", token.getToken());
		map.put("tokenType", TokenType.REFRESH_TOKEN.name());

		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, token.getTtl(), TimeUnit.SECONDS);

		return token;
	}

	public Token saveAuthToken(String tokenValue, Long userId, long ttl) {
		String key = AUTH_TOKEN.getPREFIX() + userId;

		Map<String, String> map = new HashMap<>();
		map.put("userId", String.valueOf(userId));
		map.put("token", tokenValue);
		map.put("tokenType", AUTH_TOKEN.name());

		saveToRedis(key, map, ttl);
		return Token.builder()
			.userId(userId)
			.token(tokenValue)
			.tokenType(AUTH_TOKEN)
			.ttl(ttl)
			.build();
	}

	public Token saveRegistrationToken(String tokenValue, Long userId, long ttl) {
		String key = REGISTRATION_TOKEN.getPREFIX() + tokenValue;

		Map<String, String> map = new HashMap<>();
		map.put("userId", String.valueOf(userId));
		map.put("token", tokenValue);
		map.put("tokenType", REGISTRATION_TOKEN.name());

		saveToRedis(key, map, ttl);

		return Token.builder()
			.userId(userId)
			.token(tokenValue)
			.tokenType(REGISTRATION_TOKEN)
			.ttl(ttl)
			.build();
	}

	private void saveToRedis(String key, Map<String, String> map, long ttl) {
		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
	}

	public void deleteRefreshToken(Long userId) {
		redisTemplate.delete(REFRESH_TOKEN.getPREFIX() + userId);
	}

	public void deleteRegistrationToken(String token) {
		redisTemplate.delete(REGISTRATION_TOKEN.getPREFIX() + token);
	}

	public void deleteAuthToken(Long userId) {
		redisTemplate.delete(AUTH_TOKEN.getPREFIX() + userId);
	}
}
