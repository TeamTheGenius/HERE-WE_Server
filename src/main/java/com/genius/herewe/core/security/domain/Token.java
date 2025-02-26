package com.genius.herewe.core.security.domain;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "refresh_token")
public class Token {
	@Id
	private Long userId;

	private String nickname;

	@Indexed
	private String token;

	@TimeToLive
	private long ttl;

	@Builder
	public Token(Long userId, String nickname, String token, long ttl) {
		this.userId = userId;
		this.nickname = nickname;
		this.token = token;
		this.ttl = ttl;
	}

	public static Token create(Map<Object, Object> entries) {
		return Token.builder()
			.userId(Long.valueOf((String)entries.get("userId")))
			.nickname((String)entries.get("nickname"))
			.token((String)entries.get("token"))
			.build();
	}
}
