package com.genius.herewe.core.security.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.genius.herewe.core.security.domain.Token;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepository {
	private static final String KEY_PREFIX = "refresh_token:";
	private final RedisTemplate<String, String> redisTemplate;

	public Optional<Token> findById(Long userId) {
		String key = KEY_PREFIX + userId;
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

		if (entries.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(Token.create(entries));
	}

	public Token save(Token token) {
		String key = KEY_PREFIX + token.getUserId();

		Map<String, String> map = new HashMap<>();
		map.put("userId", String.valueOf(token.getUserId()));
		map.put("nickname", token.getNickname());
		map.put("token", token.getToken());

		redisTemplate.opsForHash().putAll(key, map);
		redisTemplate.expire(key, token.getTtl(), TimeUnit.SECONDS);

		return token;
	}

	public void delete(Long userId) {
		String key = KEY_PREFIX + userId;
		redisTemplate.delete(key);
	}
}
